package com.evernote.jenkins.evernote.auto.action;

import java.io.PrintStream;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.jenkins.evernote.auto.Messages;
import com.evernote.jenkins.plugin.Autable;
import com.evernote.jenkins.plugin.Guid;
import com.evernote.jenkins.plugin.NoteStoreWrapper;

public class NotebookAction implements AutoAction {

    private static final NotebookAction INSTANCE = new NotebookAction();

    public static NotebookAction getInstance() {
        return INSTANCE;
    }

    @Override
    public String key() {
        return "Notebook";
    }

    @Override
    public void doProcess(Note note, String guid) {
        note.setNotebookGuid(guid);
    }

    @Override
    public void printLog(PrintStream printStream) {
        printStream.println(toString());
    }

    @Override
    public String description() {
        return Messages.AutoEvernote_action_description_notebook_change();
    }

    @Override
    public String toString() {
        return "'Change notebook'";
    }

    @Override
    public Autable resolve(NoteStoreWrapper noteStore, Guid guid) {
        Notebook notebook = noteStore.findNotebook(guid);
        return new com.evernote.jenkins.plugin.Notebook(notebook);
    }
}
