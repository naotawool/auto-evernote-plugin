package com.evernote.jenkins.exception;

public class EvernoteRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -690294265995260779L;

    public EvernoteRuntimeException(Exception cause) {
        super(cause);
    }
}
