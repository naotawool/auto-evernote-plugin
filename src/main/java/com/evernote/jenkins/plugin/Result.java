package com.evernote.jenkins.plugin;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.evernote.jenkins.plugin.Autable.NullAutable;

public class Result {

    private Autable autable;
    private String description;
    private List<NoteDisplay> notes;

    public Result(Autable autable, String description, List<NoteDisplay> notes) {
        this.autable = autable;
        this.description = description;
        this.notes = notes;
    }

    public Autable getAutable() {
        return autable;
    }

    public String getDescription() {
        return description;
    }

    public List<NoteDisplay> getNotes() {
        return notes;
    }

    public static class NullResult extends Result {

        private static final NullResult INSTANCE = new NullResult();

        /**
         * {@link #getInstance()}を使用してください。
         */
        @Deprecated
        public NullResult() {
            super(null, null, null);
        }

        public static NullResult getInstance() {
            return INSTANCE;
        }

        @Override
        public Autable getAutable() {
            return NullAutable.getInstance();
        }

        @Override
        public String getDescription() {
            return StringUtils.EMPTY;
        }

        @Override
        public List<NoteDisplay> getNotes() {
            return Collections.emptyList();
        }
    }
}
