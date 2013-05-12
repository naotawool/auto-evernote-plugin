package com.evernote.jenkins.evernote.auto;

import hudson.model.ParameterValue;

import java.util.ArrayList;
import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import com.evernote.jenkins.plugin.NoteDisplay;

public class TargetNotesParameterValue extends ParameterValue {

    private List<NoteDisplay> notes;

    @DataBoundConstructor
    public TargetNotesParameterValue(String name) {
        this(name, new ArrayList<NoteDisplay>(0));
    }

    public TargetNotesParameterValue(String name, List<NoteDisplay> notes) {
        super(name);
        this.notes = notes;
    }

    public void setNotes(List<NoteDisplay> notes) {
        this.notes = notes;
    }

    public List<NoteDisplay> getNotes() {
        return notes;
    }
}
