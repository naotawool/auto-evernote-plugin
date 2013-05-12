package com.evernote.jenkins.evernote.auto;

import java.io.PrintStream;

import com.evernote.edam.type.Note;

public interface ProcessType {

    public String key();

    public void doProcess(Note note, String guid);

    public void printLog(PrintStream printStream);

    public static class NullProcessType implements ProcessType {

        private final static NullProcessType INSTANCE = new NullProcessType();

        /**
         * インスタンス化を抑制。
         */
        private NullProcessType() {
            // NOP
        }

        public static NullProcessType getInstance() {
            return INSTANCE;
        }

        @Override
        public String key() {
            return null;
        }

        @Override
        public void doProcess(Note note, String guid) {
            // NOP
        }

        @Override
        public void printLog(PrintStream printStream) {
            printStream.println("No operation.");
        }
    }
}
