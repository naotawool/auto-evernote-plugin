package com.evernote.jenkins.evernote.auto;

import static com.evernote.jenkins.util.Util.not;
import hudson.Extension;
import hudson.model.ParameterValue;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.model.ParameterDefinition;
import hudson.model.ParametersDefinitionProperty;
import hudson.tasks.Builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.jenkins.plugin.NoteDisplay;

public class TargetNotesParameterDefinition extends ParameterDefinition implements
        Comparable<TargetNotesParameterDefinition> {

    private final UUID uuid;

    @DataBoundConstructor
    public TargetNotesParameterDefinition(String name, String uuid) {
        super(name);

        if (StringUtils.isEmpty(uuid)) {
            this.uuid = UUID.randomUUID();
        } else {
            this.uuid = UUID.fromString(uuid);
        }
    }

    public List<NoteDisplay> getTargetNotes() {

        @SuppressWarnings("rawtypes")
        List<AbstractProject> jobs = Jenkins.getInstance().getItems(AbstractProject.class);

        @SuppressWarnings("rawtypes")
        AbstractProject target = null;
        for (@SuppressWarnings("rawtypes")
        AbstractProject project : jobs) {
            ParametersDefinitionProperty property = getParametersProperty(project);
            if (property == null) {
                break;
            }
            if (hasSameParameterDefinition(property)) {
                target = project;
                break;
            }
        }

        if (target == null || not(FreeStyleProject.class.isInstance(target))) {
            return Collections.emptyList();
        }

        AutoActionBuilder evernoteBuilder = getBuilder((FreeStyleProject) target);
        if (evernoteBuilder == null) {
            return Collections.emptyList();
        }

        NoteList noteList = evernoteBuilder.findTargetNotes();
        List<NoteDisplay> notes = new ArrayList<>(noteList.getNotes().size());
        for (Note note : noteList.getNotes()) {
            notes.add(NoteDisplay.of(note));
        }

        return notes;
    }

    private ParametersDefinitionProperty getParametersProperty(AbstractProject<?, ?> project) {
        return (ParametersDefinitionProperty) project
                .getProperty(ParametersDefinitionProperty.class);
    }

    private AutoActionBuilder getBuilder(FreeStyleProject project) {
        for (Builder builder : project.getBuilders()) {
            if (AutoActionBuilder.class.isInstance(builder)) {
                return (AutoActionBuilder) builder;
            }
        }
        return null;
    }

    private boolean isSameParameterDefinition(ParameterDefinition pd) {
        return TargetNotesParameterDefinition.class.isInstance(pd) && //
                TargetNotesParameterDefinition.class.cast(pd).compareTo(this) == 0;
    }

    private boolean hasSameParameterDefinition(ParametersDefinitionProperty property) {

        List<ParameterDefinition> parameterDefinitions = property.getParameterDefinitions();

        if (CollectionUtils.isEmpty(parameterDefinitions)) {
            return false;
        }

        for (ParameterDefinition pd : parameterDefinitions) {
            if (isSameParameterDefinition(pd)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int compareTo(TargetNotesParameterDefinition other) {
        if (uuid.equals(other.uuid)) {
            return 0;
        }
        return -1;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req, JSONObject formData) {
        TargetNotesParameterValue value = req.bindJSON(TargetNotesParameterValue.class, formData);
        value.setNotes(getTargetNotes());
        return value;
    }

    @Override
    public ParameterValue createValue(StaplerRequest req) {
        return null;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends ParameterDescriptor {

        @Override
        public String getDisplayName() {
            return "事前に対象ノートを確認";
        }
    }
}
