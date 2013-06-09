package com.evernote.jenkins.evernote.auto.action;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import com.evernote.edam.type.Note;

/**
 * {@link TagAction}に対するテストクラス。
 *
 * @author naotake
 */
@RunWith(Enclosed.class)
public class TagActionTest {

    private static Note note;
    private static Note tagEmptyNote;

    private static void setUpNote() {
        note = new Note();
        note.addToTagGuids("tag");

        tagEmptyNote = new Note();
    }

    public static class タグを追加する場合 {

        private TagAction testee = TagAction.ADD;

        @Before
        public void setUp() {
            setUpNote();
        }

        @Test
        public void 指定したタグが追加されること() {
            testee.doProcess(note, "test-tag");
            testee.doProcess(tagEmptyNote, "test-tag");

            assertThat(note.getTagGuids(), hasSize(2));
            assertThat(note.getTagGuids(), containsInAnyOrder("tag", "test-tag"));

            assertThat(tagEmptyNote.getTagGuids(), hasSize(1));
            assertThat(tagEmptyNote.getTagGuids(), containsInAnyOrder("test-tag"));
        }
    }

    public static class タグを削除する場合 {

        private TagAction testee = TagAction.DELETE;

        @Before
        public void setUp() {
            setUpNote();
        }

        @Test
        public void 指定したタグが削除されること() {
            testee.doProcess(note, "tag");
            testee.doProcess(tagEmptyNote, "tag");

            assertThat(note.getTagGuids(), empty());

            assertThat(tagEmptyNote.getTagGuids(), nullValue());
        }
    }
}
