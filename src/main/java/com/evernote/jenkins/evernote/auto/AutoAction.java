package com.evernote.jenkins.evernote.auto;

import java.io.PrintStream;

import com.evernote.edam.type.Note;

public interface AutoAction {

    public String key();

    public void doProcess(Note note, String guid);

    public void printLog(PrintStream printStream);

    public static class NullAction implements AutoAction {

        private final static NullAction INSTANCE = new NullAction();

        /**
         * インスタンス化を抑制。
         */
        private NullAction() {
            // NOP
        }

        public static NullAction getInstance() {
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
