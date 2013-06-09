package com.evernote.jenkins.evernote.auto.action;

import java.io.PrintStream;

import com.evernote.edam.type.Note;

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
    public String toString() {
        return "'Change notebook'";
    }
}
