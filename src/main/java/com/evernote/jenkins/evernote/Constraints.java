package com.evernote.jenkins.evernote;

public class Constraints {

    /**
     * インスタンス化を抑制。
     */
    private Constraints() {
        // NOP
    }

    public static final String USER_STORE_URL_SANDBOX = "https://sandbox.evernote.com/edam/user";

    public static final String USER_STORE_URL_PRODUCTION = "https://www.evernote.com/edam/user";
}
