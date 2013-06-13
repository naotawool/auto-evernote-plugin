package com.evernote.jenkins.evernote.auto;

import hudson.model.ParameterValue;

import java.util.List;

import org.kohsuke.stapler.DataBoundConstructor;

import com.evernote.jenkins.plugin.Autable;
import com.evernote.jenkins.plugin.NoteDisplay;
import com.evernote.jenkins.plugin.Result;

public class TargetNotesParameterValue extends ParameterValue {

    private Result result;

    @DataBoundConstructor
    public TargetNotesParameterValue(String name) {
        super(name);
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<NoteDisplay> getNotes() {
        return result.getNotes();
    }

    public Autable getAutable() {
        return result.getAutable();
    }

    @Override
    public String getDescription() {
        return result.getDescription();
    }
}
