package com.evernote.jenkins.evernote.auto;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.evernote.jenkins.evernote.auto.action.NotebookAction;

public enum TargetType {

    NOTEBOOK("Notebook", "notebookGuid") {
        @Override
        public AutoAction resolveAction(JSONObject target) {
            return NotebookAction.getInstance();
        }
    },

    TAG("Tag", "tagGuid") {
        @Override
        public AutoAction resolveAction(JSONObject target) {
            return TagAction.labelOf(target.getString("tagAction"));
        }
    };

    private final String label;
    private final String guidKey;

    private TargetType(String label, String guidKey) {
        this.label = label;
        this.guidKey = guidKey;
    }

    private static final Map<String, TargetType> LABEL_MAP = new HashMap<>();
    static {
        for (TargetType type : values()) {
            LABEL_MAP.put(type.label, type);
        }
    }

    public static TargetType labelOf(String label) {
        return LABEL_MAP.get(label);
    }

    public abstract AutoAction resolveAction(JSONObject target);

    public String getGuid(JSONObject target) {
        return target.getString(guidKey);
    }

    public String getLabel() {
        return label;
    }
}
