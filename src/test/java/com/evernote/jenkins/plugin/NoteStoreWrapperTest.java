package com.evernote.jenkins.plugin;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.theInstance;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;

import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteList;
import com.evernote.edam.notestore.NoteStore;
import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.edam.userstore.UserStore;
import com.evernote.jenkins.TestUtils;

/**
 * {@link NoteStoreWrapper}に対するテストクラス。
 *
 * @author naotake
 */
public class NoteStoreWrapperTest {

    private static final String TOKEN = "token";
    private static final boolean USE_PRODUCTION = false;

    private NoteStoreWrapper testee;

    private UserStoreFactory userStoreFactory;
    private NoteStoreFactory noteStoreFactory;

    UserStore.Client userStoreMock = createMock(UserStore.Client.class);
    NoteStore.Client noteStoreMock = createMock(NoteStore.Client.class);

    /**
     * 事前準備。
     */
    @Before
    public void setUp() {
        testee = new NoteStoreWrapper(TOKEN, USE_PRODUCTION);

        userStoreFactory = createMock(UserStoreFactory.class);
        noteStoreFactory = createMock(NoteStoreFactory.class);

        testee.setUserStoreFactory(userStoreFactory);
        testee.setNoteStoreFactory(noteStoreFactory);

        userStoreMock = createMock(UserStore.Client.class);
        noteStoreMock = createMock(NoteStore.Client.class);
    }

    @Test
    public void 初期化処理が行われること() {

        // UserStore 取得の振る舞いを定義
        expect(userStoreFactory.create(USE_PRODUCTION)).andReturn(userStoreMock);

        // NoteStore 取得の振る舞いを定義
        expect(noteStoreFactory.create(TOKEN, userStoreMock)).andReturn(noteStoreMock);
        replay(userStoreFactory, noteStoreFactory);

        // 実行
        testee.initialize();

        // 検証
        NoteStore.Client result = (com.evernote.edam.notestore.NoteStore.Client) TestUtils
                .getField(testee, "noteStore");
        assertThat(result, theInstance(noteStoreMock));
        verify(userStoreFactory, noteStoreFactory);
    }

    @Test
    public void ソートされたタグの一覧を取得できること() throws Exception {
        initializeNoteStoreByMock();

        // タグ一覧取得の振る舞いを定義
        expect(noteStoreMock.listTags(TOKEN)).andReturn(createTags());
        replay(noteStoreMock);

        // 実行
        List<Tag> actuals = testee.listTags();

        // 検証
        assertThat(actuals, hasSize(4));
        assertThat(actuals.get(0).getName(), is("タグ1"));
        assertThat(actuals.get(1).getName(), is("タグ2"));
        assertThat(actuals.get(2).getName(), is("タグ3"));
        assertThat(actuals.get(3).getName(), is("タグ4"));

        verify(noteStoreMock);
    }

    @Test
    public void ソートされたノートブックの一覧を取得できること() throws Exception {
        initializeNoteStoreByMock();

        // ノートブック一覧取得の振る舞いを定義
        expect(noteStoreMock.listNotebooks(TOKEN)).andReturn(createBooks());
        replay(noteStoreMock);

        // 実行
        List<Notebook> actuals = testee.listNotebooks();

        // 検証
        assertThat(actuals, hasSize(4));
        assertThat(actuals.get(0).getName(), is("Notebook1"));
        assertThat(actuals.get(1).getName(), is("Notebook2"));
        assertThat(actuals.get(2).getName(), is("Notebook3"));
        assertThat(actuals.get(3).getName(), is("Notebook4"));

        verify(noteStoreMock);
    }

    @Test
    public void 指定した単語を含むノートの検索が行われること() throws Exception {
        initializeNoteStoreByMock();

        // ノート検索の振る舞いを定義
        NoteList noteList = new NoteList();
        Capture<NoteFilter> filterCap = new Capture<>();
        expect(noteStoreMock.findNotes(eq(TOKEN), capture(filterCap), eq(0), eq(1000))).andReturn(
                noteList);
        replay(noteStoreMock);

        // 実行
        NoteList actual = testee.findNotesByWord("FooBar");

        // 検証
        assertThat(actual, theInstance(noteList));
        assertThat(filterCap.getValue().getWords(), is("FooBar"));

        verify(noteStoreMock);
    }

    @Test
    public void 指定したノートの更新が行われること() throws Exception {
        initializeNoteStoreByMock();

        // ノート更新の振る舞いを定義
        Note note = new Note();
        expect(noteStoreMock.updateNote(eq(TOKEN), eq(note))).andReturn(note);
        replay(noteStoreMock);

        // 実行
        testee.updateNote(note);

        // 検証
        verify(noteStoreMock);
    }

    private void initializeNoteStoreByMock() {
        TestUtils.setField(testee, "noteStore", noteStoreMock);
    }

    private List<Tag> createTags() {
        List<Tag> tags = new ArrayList<>(4);
        tags.add(createTag("タグ3"));
        tags.add(createTag("タグ4"));
        tags.add(createTag("タグ2"));
        tags.add(createTag("タグ1"));
        return tags;
    }

    private Tag createTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tag;
    }

    private List<Notebook> createBooks() {
        List<Notebook> books = new ArrayList<>(4);
        books.add(createBook("Notebook2"));
        books.add(createBook("Notebook4"));
        books.add(createBook("Notebook1"));
        books.add(createBook("Notebook3"));
        return books;
    }

    private Notebook createBook(String name) {
        Notebook book = new Notebook();
        book.setName(name);
        return book;
    }
}
