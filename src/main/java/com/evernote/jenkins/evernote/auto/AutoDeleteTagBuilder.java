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
import com.evernote.jenkins.plugin.NoteStoreWrapper;

public class AutoDeleteTagBuilder extends Builder {

    private final String word;
    private final ActionType actionType;
    private final ProcessType processType;
    private final String guid;

    private transient final NoteStoreWrapper noteStore;

    @DataBoundConstructor
    public AutoDeleteTagBuilder(String word, ActionType actionType, ProcessType processType,
            String guid) {

        this.word = word;
        this.actionType = actionType;
        this.processType = processType;
        this.guid = guid;

        this.noteStore = NoteStoreWrapper.newInitializedInstance(developerToken(), useProduction());
    }

    public String getWord() {
        return word;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getAction() {
        return actionType.getLabel();
    }

    public String getTagProcessType() {
        return processType.key();
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

        listener.getLogger().println("Process type is " + processType);

        // TODO Not implemented 'Notebook' action.
        if (actionType == ActionType.NOTEBOOK) {
            build.setResult(Result.UNSTABLE);
            listener.getLogger().println("Not implemented 'Notebook' action!!");
            return true;
        }

        if (StringUtils.isEmpty(guid)) {
            build.setResult(Result.FAILURE);
            listener.getLogger().println("Target GUID is null!!");
            return false;
        }

        for (Note note : notes.getNotes()) {
            processType.doProcess(note, guid);
            processType.printLog(listener.getLogger());

            noteStore.updateNote(note);

            listener.getLogger().println("Note: " + note);
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
     * Descriptor for {@link AutoDeleteTagBuilder}. Used as a singleton. The
     * class is marked as public so that it can be accessed from views.
     *
     * @see {@code src/main/resources/com.evernote.jenkins.evernote.auto/AutoDeleteTagBuilder/*.jelly}
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
                throw new FormException("対象のノートを検索する条件を指定してください。", "word");
            }

            JSONObject target = formData.getJSONObject("target");

            ActionType actionType = ActionType.labelOf(target.getString("value"));
            if (actionType == null) {
                throw new FormException("'Tag' もしくは 'Notebook' を指定してください。", "value");
            }

            ProcessType processType = actionType.getProcessType(target);
            String guid = actionType.getGuid(target);

            return new AutoDeleteTagBuilder(word, actionType, processType, guid);
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
                return FormValidation.error("対象のノートを検索する条件を指定してください。");
            }
            return FormValidation.ok();
        }

        public ListBoxModel doFillTagProcessTypeItems() {
            ListBoxModel items = new ListBoxModel();
            for (TagProcessType type : TagProcessType.values()) {
                items.add(type.toString());
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
            NoteStoreWrapper noteStore = NoteStoreWrapper.newInitializedInstance(//
                    developerToken(), useProduction());
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
            NoteStoreWrapper noteStore = NoteStoreWrapper.newInitializedInstance(//
                    developerToken(), useProduction());
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
            return "Auto Evernote";
        }
    }
}
