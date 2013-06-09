package com.evernote.jenkins.evernote.auto.action;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.evernote.edam.type.Note;

/**
 * {@link NotebookAction}に対するテストクラス。
 *
 * @author naotake
 */
public class NotebookActionTest {

    private NotebookAction testee = NotebookAction.getInstance();

    @Test
    public void key情報を取得できること() {
        assertThat(testee.key(), is("Notebook"));
    }

    @Test
    public void 指定したノートブックのGUIDが変更されること() {
        Note note = new Note();
        note.setNotebookGuid("guid");

        testee.doProcess(note, "test-guid");
        assertThat(note.getNotebookGuid(), is("test-guid"));
    }
}
