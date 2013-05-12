package com.evernote.jenkins.evernote.auto;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.evernote.jenkins.evernote.auto.ProcessType.NullProcessType;

public enum ActionType {

    NOTEBOOK("Notebook", "notebookGuid") {

        @Override
        public ProcessType getProcessType(JSONObject target) {
            return NullProcessType.getInstance();
        }
    },

    TAG("Tag", "tagGuid") {
        @Override
        public ProcessType getProcessType(JSONObject target) {
            return TagProcessType.labelOf(target.getString("tagProcessType"));
        }
    };

    private final String label;
    private final String guidKey;

    private ActionType(String label, String guidKey) {
        this.label = label;
        this.guidKey = guidKey;
    }

    private static final Map<String, ActionType> LABEL_MAP = new HashMap<>();
    static {
        for (ActionType type : values()) {
            LABEL_MAP.put(type.label, type);
        }
    }

    public static ActionType labelOf(String label) {
        return LABEL_MAP.get(label);
    }

    public abstract ProcessType getProcessType(JSONObject target);

    public String getGuid(JSONObject target) {
        return target.getString(guidKey);
    }

    public String getLabel() {
        return label;
    }
}
