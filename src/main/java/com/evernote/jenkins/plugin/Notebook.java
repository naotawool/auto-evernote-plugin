package com.evernote.jenkins.plugin;

import static com.evernote.jenkins.util.Util.not;

public class Notebook implements Autable {

    private com.evernote.edam.type.Notebook notebook;
    private Guid guid;

    public Notebook(com.evernote.edam.type.Notebook notebook) {
        this.notebook = notebook;
        this.guid = new Guid(notebook.getGuid());
    }

    @Override
    public Guid guid() {
        return guid;
    }

    @Override
    public String type() {
        return "ノートブック";
    }

    @Override
    public String name() {
        return notebook.getName();
    }

    @Override
    public int hashCode() {
        return guid.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (not(other instanceof Notebook)) {
            return false;
        }
        return guid.equals(Notebook.class.cast(other));
    }

    @Override
    public String toString() {
        return name();
    }
}
