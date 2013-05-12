package com.evernote.jenkins.plugin;

import java.util.Collections;
import java.util.List;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.edam.userstore.UserStore;
import com.evernote.jenkins.evernote.NotebookComparator;
import com.evernote.jenkins.evernote.TagComparator;
import com.evernote.thrift.TException;

public class NoteStoreWrapper {

    private static final int MAX_NOTES = 1000;

    private final String token;
    private final NoteStore.Client noteStore;

    private NoteStoreWrapper(String token, NoteStore.Client noteStore) {
        this.token = token;
        this.noteStore = noteStore;
    }

    public static NoteStoreWrapper newInitializedInstance(String developerToken,
            boolean useProduction) {
        NoteStore.Client noteStore = initializeNoteStore(developerToken, useProduction);
        return new NoteStoreWrapper(developerToken, noteStore);
    }

    private static NoteStore.Client initializeNoteStore(String developerToken, boolean useProduction) {
        UserStore.Client userStore = new UserStoreFactory().create(useProduction);
        NoteStore.Client noteStore = new NoteStoreFactory().create(developerToken, userStore);
        return noteStore;
    }

    public List<Tag> listTags() {
        List<Tag> tags = null;
        try {
            tags = noteStore.listTags(token);
        } catch (EDAMUserException | EDAMSystemException | TException e) {
            throw new RuntimeException(e);
        }

        Collections.sort(tags, TagComparator.ASC);

        return tags;
    }

    public List<Notebook> listNotebooks() {
        List<Notebook> notebooks = null;
        try {
            notebooks = noteStore.listNotebooks(token);
        } catch (EDAMUserException | EDAMSystemException | TException e) {
            throw new RuntimeException(e);
        }

        Collections.sort(notebooks, NotebookComparator.ASC);

        return notebooks;
    }

    public NoteList findNotesByWord(String word) {

        NoteFilter filter = new NoteFilter();
        filter.setOrder(NoteSortOrder.CREATED.getValue());
        filter.setWords(word);

        return findNotes(filter);
    }

    public NoteList findNotes(NoteFilter filter) {

        NoteList noteList;
        try {
            noteList = noteStore.findNotes(token, filter, 0, MAX_NOTES);
        } catch (EDAMUserException | EDAMSystemException | EDAMNotFoundException | TException e) {
            throw new RuntimeException();
        }
        return noteList;
    }

    public Note updateNote(Note note) {
        try {
            return noteStore.updateNote(token, note);
        } catch (EDAMUserException | EDAMSystemException | EDAMNotFoundException | TException e) {
            throw new RuntimeException();
        }
    }
}
