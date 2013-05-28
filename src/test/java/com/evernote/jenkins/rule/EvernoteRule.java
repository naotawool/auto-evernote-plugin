package com.evernote.jenkins.rule;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.evernote.edam.error.EDAMNotFoundException;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.edam.userstore.UserStore;
import com.evernote.jenkins.evernote.Constraints;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

public final class EvernoteRule implements TestRule {

    private final String token;
    private final boolean useProduction;

    private NoteStore.Client noteStore;

    private EvernoteRule(String token, boolean useProduction) {
        this.token = token;
        this.useProduction = useProduction;
    }

    public static EvernoteRule asProduction(String token) {
        return new EvernoteRule(token, true);
    }

    public static EvernoteRule asSandbox(String token) {
        return new EvernoteRule(token, false);
    }

    @Override
    public Statement apply(final Statement base, Description description) {

        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                before();
                base.evaluate();
            }
        };
    }

    public Note updateNote(Note note) throws Exception {
        try {
            return noteStore.updateNote(token, note);
        } catch (EDAMUserException | EDAMSystemException | EDAMNotFoundException | TException e) {
            throw new Exception(e);
        }
    }

    public Note resolveNoteByTitle(final String noteTitle) throws Exception {

        NoteFilter filter = new NoteFilter();
        filter.setWords("intitle:" + noteTitle);

        NoteList noteList = null;
        try {
            noteList = noteStore.findNotes(token, filter, 0, 1);
        } catch (EDAMUserException | EDAMSystemException | EDAMNotFoundException | TException e) {
            throw new Exception(e);
        }

        return selectNullIfEmpty(noteList.getNotes());
    }

    public Tag resolveTagByName(final String tagName) throws Exception {
        List<Tag> tags = null;
        try {
            tags = noteStore.listTags(token);
        } catch (EDAMUserException | EDAMSystemException | TException e) {
            throw new Exception(e);
        }

        List<Tag> finds = findObject(tags, new Predicate<Tag>() {

            @Override
            public boolean apply(@Nullable Tag input) {
                return StringUtils.equals(input.getName(), tagName);
            }
        });
        return selectNullIfEmpty(finds);
    }

    public Notebook resolveNotebookByName(final String notebookName) throws Exception {
        List<Notebook> notebooks = null;
        try {
            notebooks = noteStore.listNotebooks(token);
        } catch (EDAMUserException | EDAMSystemException | TException e) {
            throw new Exception(e);
        }

        List<Notebook> finds = findObject(notebooks, new Predicate<Notebook>() {

            @Override
            public boolean apply(@Nullable Notebook input) {
                return StringUtils.equals(input.getName(), notebookName);
            }
        });
        return selectNullIfEmpty(finds);
    }

    private <T> List<T> findObject(List<T> list, Predicate<T> predicate) {
        Collection<T> results = Collections2.filter(list, predicate);
        return Lists.newArrayList(results);
    }

    private <T> T selectNullIfEmpty(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    protected void before() throws Exception {
        THttpClient userStoreTrans;
        try {
            userStoreTrans = new THttpClient(resolveUrl());
        } catch (TTransportException e) {
            throw new Exception(e);
        }
        TBinaryProtocol userStoreProtocol = new TBinaryProtocol(userStoreTrans);

        UserStore.Client userStore = new UserStore.Client(userStoreProtocol);

        String noteStoreUrl;
        try {
            noteStoreUrl = userStore.getNoteStoreUrl(token);
        } catch (EDAMUserException | EDAMSystemException | TException e) {
            throw new Exception(e);
        }

        THttpClient noteStoreTrans;
        try {
            noteStoreTrans = new THttpClient(noteStoreUrl);
        } catch (TTransportException e) {
            throw new Exception(e);
        }
        TBinaryProtocol noteStoreProt = new TBinaryProtocol(noteStoreTrans);

        noteStore = new NoteStore.Client(noteStoreProt);
    }

    private String resolveUrl() {
        if (useProduction) {
            throw new NotImplementedException("Not support Production environment.");
        }
        return Constraints.USER_STORE_URL_SANDBOX;
    }
}
