package com.evernote.jenkins.plugin;

import static com.evernote.jenkins.util.Util.not;

public class Guid {

    private String guid;

    public Guid(String guid) {
        this.guid = guid;
    }

    public String get() {
        return guid;
    }

    @Override
    public int hashCode() {
        return guid.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (not(other instanceof String)) {
            return false;
        }
        return String.class.cast(other).equals(guid);
    }

    @Override
    public String toString() {
        return guid;
    }
}
