package com.evernote.jenkins.evernote.auto;

import hudson.Extension;
import hudson.util.FormValidation;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.evernote.jenkins.exception.EvernoteRuntimeException;
import com.evernote.jenkins.plugin.NoteStoreWrapper;

@Extension
public class EvernoteGlobalConfiguration extends GlobalConfiguration {

    private String developerToken;
    private boolean production;

    public static EvernoteGlobalConfiguration get() {
        return GlobalConfiguration.all().get(EvernoteGlobalConfiguration.class);
    }

    public EvernoteGlobalConfiguration() {
        load();
    }

    public String getDeveloperToken() {
        return developerToken;
    }

    public void setDeveloperToken(String developerToken) {
        this.developerToken = developerToken;
        save();
    }

    public boolean getProduction() {
        return production;
    }

    public void setProduction(boolean production) {
        this.production = production;
        save();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        return true;
    }

    public FormValidation doCheckDeveloperToken(@QueryParameter String value) {
        if (StringUtils.isEmpty(value)) {
            return FormValidation.warning(Messages.AutoEvernote_required_developerToken());
        }
        return FormValidation.ok();
    }

    public FormValidation doVerifyDeveloperToken(
            @QueryParameter("developerToken") final String developerToken,
            @QueryParameter("production") boolean useProduction) {

        NoteStoreWrapper noteStore = new NoteStoreWrapper(developerToken, useProduction);
        try {
            noteStore.initialize();
        } catch (EvernoteRuntimeException e) {
            return FormValidation.error(Messages.AutoEvernote_validate_developerToken_error());
        }
        return FormValidation.ok(Messages.AutoEvernote_validate_developerToken_ok());
    }
}
