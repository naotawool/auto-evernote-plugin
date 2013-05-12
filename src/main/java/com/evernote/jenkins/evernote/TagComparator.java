package com.evernote.jenkins.evernote;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import com.evernote.edam.type.Tag;

/**
 * {@link Tag}を比較する{@link Comparator}。
 *
 * @author naotake
 */
public class TagComparator implements Comparator<Tag> {

    /** タグ名の昇順 */
    public static TagComparator ASC = new TagComparator(true);

    /** タグ名の降順 */
    public static TagComparator DESC = new TagComparator(false);

    private boolean ascending;

    /**
     * 外部からのインスタンス化を抑制。
     *
     * @param ascending 昇順かどうか
     */
    private TagComparator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(Tag tag1, Tag tag2) {

        String tagName1 = StringUtils.defaultString(tag1.getName());
        String tagName2 = StringUtils.defaultString(tag2.getName());

        int result = tagName1.compareTo(tagName2);
        if (ascending) {
            return result;
        } else {
            return result * -1;
        }
    }
}
