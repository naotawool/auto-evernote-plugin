package com.evernote.jenkins.evernote.auto.entry;

import static com.evernote.jenkins.util.Util.not;

import org.apache.commons.lang.StringUtils;

/**
 * 処理対象のノートを検索するための情報を保持するクラス。
 *
 * @author naotake
 */
public class Word {

    private final String word;

    private Word(String word) {
        this.word = word;
    }

    public static Word of(String word) {
        return new Word(StringUtils.defaultString(word));
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(word);
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    public boolean equals(Object other) {
        if (not(other instanceof Word)) {
            return false;
        }
        return Word.class.cast(other).toString().equals(word);
    }

    @Override
    public String toString() {
        return word;
    }

}
