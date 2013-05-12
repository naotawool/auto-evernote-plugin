package com.evernote.jenkins.plugin;

import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.userstore.UserStore;
import com.evernote.thrift.TException;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;

/**
 * {@link NoteStore}を生成するファクトリクラス。
 *
 * @author naotake
 */
class NoteStoreFactory {

    /**
     * 指定したユーザの{@link UserStore.Client}を生成する。
     *
     * @param token Developer Token
     * @param userStore {@link NoteStore.Client}
     * @return {@link UserStore.Client}
     */
    public NoteStore.Client create(String token, UserStore.Client userStore) {

        String noteStoreUrl;
        try {
            noteStoreUrl = userStore.getNoteStoreUrl(token);
        } catch (EDAMUserException | EDAMSystemException | TException e) {
            throw new RuntimeException(e);
        }

        THttpClient noteStoreTrans;
        try {
            noteStoreTrans = new THttpClient(noteStoreUrl);
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
        TBinaryProtocol noteStoreProt = new TBinaryProtocol(noteStoreTrans);

        return new NoteStore.Client(noteStoreProt);
    }
}
