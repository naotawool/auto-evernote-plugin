package com.evernote.jenkins.evernote.auto;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import hudson.util.FormValidation;
import hudson.util.FormValidation.Kind;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

/**
 * {@link EvernoteGlobalConfiguration}に対するテストクラス。
 *
 * @author naotake
 */
public class EvernoteGlobalConfigurationTest {

    private EvernoteGlobalConfiguration testee;

    @Rule
    public JenkinsRule rule = new JenkinsRule();

    /**
     * 事前処理。
     */
    @Before
    public void setUp() {
        testee = new EvernoteGlobalConfiguration();
    }

    @Test
    public void DeveloperToken未指定の場合にFormValidationの_warning_が返されること() {
        FormValidation result = testee.doCheckDeveloperToken("");
        assertThat(result.kind, is(Kind.WARNING));
        assertThat(result.getMessage(), is(Messages.AutoEvernote_required_developerToken()));
    }

    @Test
    public void DeveloperToken指定の場合にFormValidationの_ok_が返されること() {
        FormValidation result = testee.doCheckDeveloperToken("token");
        assertThat(result.kind, is(Kind.OK));
        assertThat(result.getMessage(), nullValue());
    }
}
