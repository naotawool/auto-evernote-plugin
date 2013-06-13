package com.evernote.jenkins.evernote.auto;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.jenkins.evernote.auto.action.AutoAction;
import com.evernote.jenkins.evernote.auto.action.TagAction;
import com.evernote.jenkins.plugin.Autable;
import com.evernote.jenkins.plugin.Guid;
import com.evernote.jenkins.plugin.NoteDisplay;
import com.evernote.jenkins.plugin.NoteStoreWrapper;

public class AutoActionBuilder extends Builder {

    private final String word;
    private final TargetType targetType;
    private final AutoAction autoAction;
    private final String guid;

    private transient final NoteStoreWrapper noteStore;

    @DataBoundConstructor
    public AutoActionBuilder(String word, TargetType targetType, AutoAction autoAction, String guid) {

        this.word = word;
        this.targetType = targetType;
        this.autoAction = autoAction;
        this.guid = guid;

        this.noteStore = new NoteStoreWrapper(developerToken(), useProduction());
        this.noteStore.initialize();
    }

    public String getWord() {
        return word;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public String getTarget() {
        return targetType.getLabel();
    }

    public AutoAction getAutoAction() {
        return autoAction;
    }

    public Autable getAutable() {
        return autoAction.resolve(noteStore, new Guid(guid));
    }

    public String getTagActionAsString() {
        return autoAction.key();
    }

    public String getNotebookGuid() {
        return guid;
    }

    public String getTagGuid() {
        return guid;
    }

    private static String developerToken() {
        return EvernoteGlobalConfiguration.get().getDeveloperToken();
    }

    private static boolean useProduction() {
        return EvernoteGlobalConfiguration.get().getProduction();
    }

    @Override
    public boolean perform(@SuppressWarnings("rawtypes") AbstractBuild build, Launcher launcher,
            BuildListener listener) {

        NoteList notes = findTargetNotes();

        listener.getLogger().println("Process type is " + autoAction);

        if (StringUtils.isEmpty(guid)) {
            build.setResult(Result.FAILURE);
            listener.getLogger().println("Target GUID is null!!");
            return false;
        }

        for (Note note : notes.getNotes()) {
            autoAction.doProcess(note, guid);
            autoAction.printLog(listener.getLogger());

            noteStore.updateNote(note);

            listener.getLogger().println("Note: " + NoteDisplay.of(note));
        }

        return true;
    }

    public NoteList findTargetNotes() {
        return noteStore.findNotesByWord(word);
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link AutoActionBuilder}. Used as a singleton. The class
     * is marked as public so that it can be accessed from views.
     *
     * @see {@code src/main/resources/com.evernote.jenkins.evernote.auto/AutoActionBuilder/*.jelly}
     */
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * To persist global configuration information, simply store it in a
         * field and call save().
         *
         * <p>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        @Override
        public Builder newInstance(StaplerRequest req, JSONObject formData) throws FormException {
            String word = formData.getString("word");
            if (StringUtils.isEmpty(word)) {
                throw new FormException(Messages.AutoEvernote_required_developerToken(), "word");
            }

            JSONObject target = formData.getJSONObject("target");

            TargetType targetType = TargetType.labelOf(target.getString("value"));
            if (targetType == null) {
                throw new FormException(Messages.AutoEvernote_required_actionType(), "value");
            }

            AutoAction action = targetType.resolveAction(target);
            String guid = targetType.getGuid(target);

            return new AutoActionBuilder(word, targetType, action, guid);
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         *
         * @param value This parameter receives the value that the user has
         *        typed.
         * @return Indicates the outcome of the validation. This is sent to the
         *         browser.
         */
        public FormValidation doCheckWord(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error(Messages.AutoEvernote_required_searchTargetWord());
            }
            return FormValidation.ok();
        }

        public ListBoxModel doFillTagActionItems() {
            ListBoxModel items = new ListBoxModel();
            for (TagAction action : TagAction.values()) {
                items.add(action.toString());
            }
            return items;
        }

        public ListBoxModel doFillTagGuidItems() {
            ListBoxModel items = new ListBoxModel();
            for (Tag tag : getTags()) {
                items.add(tag.getName(), tag.getGuid());
            }
            return items;
        }

        private List<Tag> getTags() {
            NoteStoreWrapper noteStore = new NoteStoreWrapper(developerToken(), useProduction());
            noteStore.initialize();
            return noteStore.listTags();
        }

        public ListBoxModel doFillNotebookGuidItems() {
            ListBoxModel items = new ListBoxModel();
            for (Notebook notebook : getNotebooks()) {
                items.add(notebook.getName(), notebook.getGuid());
            }
            return items;
        }

        private List<Notebook> getNotebooks() {
            NoteStoreWrapper noteStore = new NoteStoreWrapper(developerToken(), useProduction());
            noteStore.initialize();
            return noteStore.listNotebooks();
        }

        public boolean isApplicable(
                @SuppressWarnings("rawtypes") Class<? extends AbstractProject> aClass) {
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return Messages.AutoActionBuilder_displayName();
        }
    }
}
