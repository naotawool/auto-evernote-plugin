package com.evernote.jenkins.plugin;

import org.apache.commons.lang.time.FastDateFormat;

import com.evernote.edam.type.Note;

public class NoteDisplay {

    private static final FastDateFormat FORMATTER = FastDateFormat
            .getInstance("yyyy/MM/dd HH:mm:ss");

    private final Note note;

    private NoteDisplay(Note note) {
        this.note = note;
    }

    public static NoteDisplay of(Note note) {
        return new NoteDisplay(note);
    }

    public String getTitle() {
        return note.getTitle();
    }

    public String getCreated() {
        return formatCreated();
    }

    public String getUpdated() {
        return formatUpdated();
    }

    private String formatCreated() {
        return FORMATTER.format(note.getCreated());
    }

    private String formatUpdated() {
        return FORMATTER.format(note.getUpdated());
    }

    @Override
    public String toString() {
        return String.format("%s(作成日: %s, 最終更新日: %s)", note.getTitle(), formatCreated(),
                formatUpdated());
    }
}
