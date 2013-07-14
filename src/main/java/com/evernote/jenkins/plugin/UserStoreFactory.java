package com.evernote.jenkins.plugin;

import com.evernote.edam.userstore.UserStore;
import com.evernote.jenkins.evernote.Constraints;
import com.evernote.thrift.protocol.TBinaryProtocol;
import com.evernote.thrift.transport.THttpClient;
import com.evernote.thrift.transport.TTransportException;

/**
 * {@link UserStore}を生成するファクトリクラス。
 *
 * @author naotake
 */
class UserStoreFactory {

    /**
     * {@link UserStore.Client}を生成する。
     *
     * @param useProduction 本番環境を使用するかどうか
     * @return {@link UserStore.Client}
     */
    public UserStore.Client create(boolean useProduction) {

        THttpClient userStoreTrans;
        try {
            userStoreTrans = new THttpClient(resolveUrl(useProduction));
        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
        TBinaryProtocol userStoreProtocol = new TBinaryProtocol(userStoreTrans);

        return new UserStore.Client(userStoreProtocol);
    }

    private String resolveUrl(boolean useProduction) {
        if (useProduction) {
            return Constraints.USER_STORE_URL_PRODUCTION;
        }
        return Constraints.USER_STORE_URL_SANDBOX;
    }
}
