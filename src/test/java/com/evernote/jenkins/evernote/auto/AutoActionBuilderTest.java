package com.evernote.jenkins.evernote.auto;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import hudson.model.FreeStyleBuild;
import hudson.model.Cause.UserIdCause;
import hudson.model.FreeStyleProject;
import jenkins.model.GlobalConfiguration;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.JenkinsRule;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.Notebook;
import com.evernote.edam.type.Tag;
import com.evernote.jenkins.evernote.auto.action.NotebookAction;
import com.evernote.jenkins.evernote.auto.action.TagAction;
import com.evernote.jenkins.evernote.auto.entry.Word;
import com.evernote.jenkins.rule.EvernoteRule;

/**
 * {@link AutoActionBuilder}に対するテストクラス。
 *
 * @author naotake
 */
@RunWith(Enclosed.class)
public class AutoActionBuilderTest {

    private static final String TOKEN = System.getProperty("token");

    public static class タグを追加する場合 {

        @Rule
        public JenkinsRule rule = new JenkinsRule();

        @Rule
        public EvernoteRule evernote = EvernoteRule.asSandbox(TOKEN);

        private Tag presentTag;
        private Note targetNote1;
        private Note targetNote2;

        @Before
        public void setUpTarget() throws Exception {
            presentTag = evernote.resolveTagByName("Amazon");
            targetNote1 = evernote.resolveNoteByTitle("AmazonTestNote");
            targetNote2 = evernote.resolveNoteByTitle("Amazon.co.jp");
        }

        @After
        public void takeBackNote() throws Exception {
            targetNote1.getTagGuids().remove(presentTag.getGuid());
            targetNote2.getTagGuids().remove(presentTag.getGuid());

            evernote.updateNote(targetNote1);
            evernote.updateNote(targetNote2);
        }

        @Test
        @SuppressWarnings("deprecation")
        public void Amazonという単語を含むノートに対して_Amazonタグ_が付与されること() throws Exception {
            // 事前にノートの状態を検証
            assertThat(targetNote1.getTagGuids(), not(contains(presentTag.getGuid())));
            assertThat(targetNote2.getTagGuids(), not(contains(presentTag.getGuid())));

            // 設定情報を追加
            GlobalConfiguration.all().add(createConfig());

            // プロジェクトの用意
            FreeStyleProject project = rule.createFreeStyleProject();
            project.getBuildersList().add(createBuilder());

            // ビルド実行
            FreeStyleBuild build = project.scheduleBuild2(0, new UserIdCause()).get();
            build.run();

            // ログを検証
            String log = FileUtils.readFileToString(build.getLogFile());
            assertThat(log, containsString("Note: AmazonTestNote"));
            assertThat(log, containsString("Note: Amazon.co.jp"));

            // ビルド実行後のノートの状態を検証
            setUpTarget();
            assertThat(targetNote1.getTagGuids(), contains(presentTag.getGuid()));
            assertThat(targetNote2.getTagGuids(), contains(presentTag.getGuid()));
        }

        private AutoActionBuilder createBuilder() {
            return new AutoActionBuilder(Word.of("Amazon"), TargetType.TAG, TagAction.ADD,
                    presentTag.getGuid());
        }
    }

    public static class タグを削除する場合 {

        @Rule
        public JenkinsRule rule = new JenkinsRule();

        @Rule
        public EvernoteRule evernote = EvernoteRule.asSandbox(TOKEN);

        private Tag presentTag;
        private Note targetNote1;
        private Note targetNote2;

        @Before
        public void setUpTarget() throws Exception {
            presentTag = evernote.resolveTagByName("Amazon");
            targetNote1 = evernote.resolveNoteByTitle("AmazonTestNote");
            targetNote2 = evernote.resolveNoteByTitle("Amazon.co.jp");
        }

        private void setUpNote() throws Exception {
            targetNote1.addToTagGuids(presentTag.getGuid());
            targetNote2.addToTagGuids(presentTag.getGuid());

            evernote.updateNote(targetNote1);
            evernote.updateNote(targetNote2);
        }

        @Test
        @SuppressWarnings("deprecation")
        public void Amazonという単語を含むノートから_Amazonタグ_が削除されること() throws Exception {
            // 事前にノートにタグを付与
            setUpNote();

            // 事前にノートの状態を検証
            assertThat(targetNote1.getTagGuids(), contains(presentTag.getGuid()));
            assertThat(targetNote2.getTagGuids(), contains(presentTag.getGuid()));

            // 設定情報を追加
            GlobalConfiguration.all().add(createConfig());

            // プロジェクトの用意
            FreeStyleProject project = rule.createFreeStyleProject();
            project.getBuildersList().add(createBuilder());

            // ビルド実行
            FreeStyleBuild build = project.scheduleBuild2(0, new UserIdCause()).get();
            build.run();

            // ログを検証
            String log = FileUtils.readFileToString(build.getLogFile());
            assertThat(log, containsString("Note: AmazonTestNote"));
            assertThat(log, containsString("Note: Amazon.co.jp"));

            // ビルド実行後のノートの状態を検証
            setUpTarget();
            assertThat(targetNote1.getTagGuids(), not(contains(presentTag.getGuid())));
            assertThat(targetNote2.getTagGuids(), not(contains(presentTag.getGuid())));
        }

        private AutoActionBuilder createBuilder() {
            return new AutoActionBuilder(Word.of("Amazon"), TargetType.TAG, TagAction.DELETE,
                    presentTag.getGuid());
        }
    }

    public static class ノートブックを変更する場合 {

        @Rule
        public JenkinsRule rule = new JenkinsRule();

        @Rule
        public EvernoteRule evernote = EvernoteRule.asSandbox(TOKEN);

        private Notebook changeNotebook;
        private Notebook defaultNotebook;
        private Note targetNote1;
        private Note targetNote2;

        @Before
        public void setUpTarget() throws Exception {
            changeNotebook = evernote.resolveNotebookByName("AmazonBook");
            defaultNotebook = evernote.resolveDefaultNotebook();
            targetNote1 = evernote.resolveNoteByTitle("AmazonTestNote");
            targetNote2 = evernote.resolveNoteByTitle("Amazon.co.jp");
        }

        @After
        public void takeBackNote() throws Exception {
            targetNote1.setNotebookGuid(defaultNotebook.getGuid());
            targetNote2.setNotebookGuid(defaultNotebook.getGuid());

            evernote.updateNote(targetNote1);
            evernote.updateNote(targetNote2);
        }

        @Test
        @SuppressWarnings("deprecation")
        public void Amazonという単語を含むノートのノートブックが_AmazonBook_に変更されること() throws Exception {
            // 事前にノートの状態を検証
            assertThat(targetNote1.getNotebookGuid(), is(defaultNotebook.getGuid()));
            assertThat(targetNote2.getNotebookGuid(), is(defaultNotebook.getGuid()));

            // 設定情報を追加
            GlobalConfiguration.all().add(createConfig());

            // プロジェクトの用意
            FreeStyleProject project = rule.createFreeStyleProject();
            project.getBuildersList().add(createBuilder());

            // ビルド実行
            FreeStyleBuild build = project.scheduleBuild2(0, new UserIdCause()).get();
            build.run();

            // ログを検証
            String log = FileUtils.readFileToString(build.getLogFile());
            assertThat(log, containsString("Note: AmazonTestNote"));
            assertThat(log, containsString("Note: Amazon.co.jp"));

            // ビルド実行後のノートの状態を検証
            setUpTarget();
            assertThat(targetNote1.getNotebookGuid(), is(changeNotebook.getGuid()));
            assertThat(targetNote2.getNotebookGuid(), is(changeNotebook.getGuid()));
        }

        private AutoActionBuilder createBuilder() {
            return new AutoActionBuilder(Word.of("Amazon"), TargetType.NOTEBOOK,
                    NotebookAction.getInstance(), changeNotebook.getGuid());
        }
    }

    private static EvernoteGlobalConfiguration createConfig() {
        EvernoteGlobalConfiguration config = new EvernoteGlobalConfiguration();
        config.setDeveloperToken(TOKEN);
        config.setProduction(false);
        return config;
    }
}
