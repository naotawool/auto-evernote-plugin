package com.evernote.jenkins.evernote.auto.entry;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

/**
 * {@link Word}に対するテストクラス。
 *
 * @author naotake
 */
@RunWith(Enclosed.class)
public class WordTest {

    public static class 検索条件が指定された場合 {

        private Word testee = Word.of("Amazon");

        @Test
        public void toString_で検索条件を取得できること() {
            assertThat(testee.toString(), is("Amazon"));
        }

        @Test
        public void isEmpty_で_false_が返されること() {
            assertThat(testee.isEmpty(), is(false));
        }

        @Test
        public void 同じ検索条件を指定した場合_equals_で_true_が返されること() {
            Word other = Word.of("Amazon");
            assertThat(testee.equals(other), is(true));
        }

        @Test
        public void 異なる検索条件を指定した場合_equals_で_false_が返されること() {
            Word other = Word.of("楽天");
            assertThat(testee.equals(other), is(false));
        }
    }

    public static class 検索条件に空文字を指定した場合 {

        private Word testee = Word.of("");

        @Test
        public void toString_で検索条件を取得できること() {
            assertThat(testee.toString(), is(""));
        }

        @Test
        public void isEmpty_で_true_が返されること() {
            assertThat(testee.isEmpty(), is(true));
        }

        @Test
        public void 同じ検索条件を指定した場合_equals_で_true_が返されること() {
            Word other = Word.of("");
            assertThat(testee.equals(other), is(true));
        }

        @Test
        public void 異なる検索条件を指定した場合_equals_で_false_が返されること() {
            Word other = Word.of("楽天");
            assertThat(testee.equals(other), is(false));
        }
    }

    public static class 検索条件に_null_を指定した場合 {

        private Word testee = Word.of(null);

        @Test
        public void toString_で検索条件を取得できること() {
            assertThat(testee.toString(), is(""));
        }

        @Test
        public void isEmpty_で_true_が返されること() {
            assertThat(testee.isEmpty(), is(true));
        }

        @Test
        public void 同じ検索条件を指定した場合_equals_で_true_が返されること() {
            Word other = Word.of(null);
            assertThat(testee.equals(other), is(true));
        }

        @Test
        public void 異なる検索条件を指定した場合_equals_で_false_が返されること() {
            Word other = Word.of("楽天");
            assertThat(testee.equals(other), is(false));
        }
    }
}
