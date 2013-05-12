package com.evernote.jenkins.evernote;

import java.util.Comparator;

import org.apache.commons.lang.StringUtils;

import com.evernote.edam.type.Notebook;

/**
 * {@link Notebook}を比較する{@link Comparator}。
 *
 * @author naotake
 */
public class NotebookComparator implements Comparator<Notebook> {

    /** タグ名の昇順 */
    public static NotebookComparator ASC = new NotebookComparator(true);

    /** タグ名の降順 */
    public static NotebookComparator DESC = new NotebookComparator(false);

    private boolean ascending;

    /**
     * 外部からのインスタンス化を抑制。
     *
     * @param ascending 昇順かどうか
     */
    private NotebookComparator(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public int compare(Notebook notebook1, Notebook notebook2) {

        String bookName1 = StringUtils.defaultString(notebook1.getName());
        String bookName2 = StringUtils.defaultString(notebook2.getName());

        int result = bookName1.compareTo(bookName2);
        if (ascending) {
            return result;
        } else {
            return result * -1;
        }
    }
}
