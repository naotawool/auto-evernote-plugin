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
    private final boolean useProduction;
    private NoteStore.Client noteStore;

    private UserStoreFactory userStoreFactory;
    private NoteStoreFactory noteStoreFactory;

    public NoteStoreWrapper(String developerToken, boolean useProduction) {
        this.token = developerToken;
        this.useProduction = useProduction;
    }

    public void initialize() {
        initializeFactory();

        UserStore.Client userStore = userStoreFactory.create(useProduction);
        NoteStore.Client noteStore = noteStoreFactory.create(token, userStore);

        this.noteStore = noteStore;
    }

    private void initializeFactory() {
        if (this.userStoreFactory == null) {
            this.userStoreFactory = new UserStoreFactory();
        }
        if (this.noteStoreFactory == null) {
            this.noteStoreFactory = new NoteStoreFactory();
        }
    }

    void setUserStoreFactory(UserStoreFactory userStoreFactory) {
        this.userStoreFactory = userStoreFactory;
    }

    void setNoteStoreFactory(NoteStoreFactory noteStoreFactory) {
        this.noteStoreFactory = noteStoreFactory;
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
            throw new RuntimeException(e);
        }
        return noteList;
    }

    public long countNotesByWord(String word) {
        NoteList noteList = findNotesByWord(word);
        return noteList.getNotes().size();
    }

    public Note updateNote(Note note) {
        try {
            return noteStore.updateNote(token, note);
        } catch (EDAMUserException | EDAMSystemException | EDAMNotFoundException | TException e) {
            throw new RuntimeException(e);
        }
    }

    public Notebook findNotebook(Guid guid) {
        try {
            return noteStore.getNotebook(token, guid.get());
        } catch (EDAMUserException | EDAMSystemException | EDAMNotFoundException | TException e) {
            throw new RuntimeException(e);
        }
    }

    public Tag findTag(Guid guid) {
        try {
            return noteStore.getTag(token, guid.get());
        } catch (EDAMUserException | EDAMSystemException | EDAMNotFoundException | TException e) {
            throw new RuntimeException(e);
        }
    }
}
