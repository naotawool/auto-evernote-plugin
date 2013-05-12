package com.evernote.jenkins.plugin;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.evernote.edam.type.Note;
import com.evernote.jenkins.TestUtils;

/**
 * {@link NoteDisplay}に対するテストクラス。
 *
 * @author naotake
 */
public class NoteDisplayTest {

    private NoteDisplay testee;

    @Test
    public void toStringでノートの情報を取得できること() {
        Note note = create("テストノート");
        note.setCreated(TestUtils.createDate(2013, 5, 1).getTime());
        note.setUpdated(TestUtils.createDate(2013, 5, 13).getTime());

        testee = NoteDisplay.of(note);
        String result = testee.toString();

        assertThat(result, is("テストノート(作成日: 2013/05/01 00:00:00, 最終更新日: 2013/05/13 00:00:00)"));
    }

    private Note create(String title) {
        Note note = new Note();
        note.setTitle(title);
        return note;
    }
}
