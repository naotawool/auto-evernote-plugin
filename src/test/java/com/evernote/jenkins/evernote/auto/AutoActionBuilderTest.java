package com.evernote.jenkins.evernote.auto;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
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
import org.jvnet.hudson.test.JenkinsRule;

import com.evernote.edam.type.Note;
import com.evernote.edam.type.Tag;
import com.evernote.jenkins.rule.EvernoteRule;

/**
 * {@link AutoActionBuilder}に対するテストクラス。
 *
 * @author naotake
 */
public class AutoActionBuilderTest {

    private static final String TOKEN = System.getProperty("token");

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
        return new AutoActionBuilder("Amazon", TargetType.TAG, TagAction.ADD, presentTag.getGuid());
    }

    private EvernoteGlobalConfiguration createConfig() {
        EvernoteGlobalConfiguration config = new EvernoteGlobalConfiguration();
        config.setDeveloperToken(TOKEN);
        config.setProduction(false);
        return config;
    }
}
