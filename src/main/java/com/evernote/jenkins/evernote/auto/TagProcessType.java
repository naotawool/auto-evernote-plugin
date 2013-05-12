package com.evernote.jenkins.evernote.auto;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.evernote.edam.type.Note;

public enum TagProcessType implements ProcessType {

    ADD("Add") {
        @Override
        public void doProcess(Note note, String guid) {
            List<String> tagGuis = note.getTagGuids();
            if (CollectionUtils.isEmpty(tagGuis)) {
                tagGuis = new ArrayList<>(1);
            }
            tagGuis.add(guid);
            note.setTagGuids(tagGuis);
        }

        @Override
        public void printLog(PrintStream printStream) {
            printStream.println("Tag added!!");
        }
    },

    DELETE("Delete") {
        @Override
        public void doProcess(Note note, String guid) {
            List<String> tagGuis = note.getTagGuids();
            if (CollectionUtils.isEmpty(tagGuis)) {
                return;
            }
            tagGuis.remove(guid);
            note.setTagGuids(tagGuis);
        }

        @Override
        public void printLog(PrintStream printStream) {
            printStream.println("Tag deleted!!");
        }
    };

    private final String label;

    private TagProcessType(final String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public String key() {
        return label;
    }

    private static final Map<String, TagProcessType> LABEL_MAP = new HashMap<>();
    static {
        for (TagProcessType type : values()) {
            LABEL_MAP.put(type.label, type);
        }
    }

    public static TagProcessType labelOf(String label) {
        return LABEL_MAP.get(label);
    }
}
