package com.evernote.jenkins.plugin;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.core.IsSame.theInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.evernote.edam.notestore.NoteStore;
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
    }

    @Test
    public void 初期化処理が行われること() {

        UserStore.Client userStoreMock = createMock(UserStore.Client.class);
        NoteStore.Client noteStoreMock = createMock(NoteStore.Client.class);

        // UserStore 取得の振る舞いを定義
        expect(userStoreFactory.create(USE_PRODUCTION)).andReturn(userStoreMock);

        // NoteStore 取得の振る舞いを定義
        expect(noteStoreFactory.create(TOKEN, userStoreMock)).andReturn(noteStoreMock);
        replay(userStoreFactory, noteStoreFactory);

        // 実行
        testee.initialize();

        // 検証
        NoteStore.Client result = (com.evernote.edam.notestore.NoteStore.Client) TestUtils.getField(testee, "noteStore");
        assertThat(result, theInstance(noteStoreMock));
        verify(userStoreFactory, noteStoreFactory);
    }
}
