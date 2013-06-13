package com.evernote.jenkins.plugin;

import static com.evernote.jenkins.util.Util.not;

public class Tag implements Autable {

    private com.evernote.edam.type.Tag tag;
    private Guid guid;

    public Tag(com.evernote.edam.type.Tag tag) {
        this.tag = tag;
        this.guid = new Guid(tag.getGuid());
    }

    @Override
    public Guid guid() {
        return guid;
    }

    @Override
    public String type() {
        return "タグ";
    }

    @Override
    public String name() {
        return tag.getName();
    }

    @Override
    public int hashCode() {
        return guid.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (not(other instanceof Tag)) {
            return false;
        }
        return guid.equals(Tag.class.cast(other));
    }

    @Override
    public String toString() {
        return name();
    }
}
