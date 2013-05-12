package com.evernote.jenkins.evernote;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.evernote.edam.type.Notebook;

/**
 * {@link NotebookComparator}に対するテストクラス。
 *
 * @author naotake
 */
public class NotebookComparatorTest {

    @Test
    public void ノートブック名の昇順に並び替えられること() {
        List<Notebook> books = createTags();
        Collections.sort(books, NotebookComparator.ASC);

        // 検証
        assertThat(books.get(0).getName(), is("Book1"));
        assertThat(books.get(1).getName(), is("Book2"));
        assertThat(books.get(2).getName(), is("Book3"));
        assertThat(books.get(3).getName(), is("Book4"));
        assertThat(books.get(4).getName(), is("ノートブック5"));
    }

    @Test
    public void ノートブック名の降順に並び替えられること() {
        List<Notebook> books = createTags();
        Collections.sort(books, NotebookComparator.DESC);

        // 検証
        assertThat(books.get(0).getName(), is("ノートブック5"));
        assertThat(books.get(1).getName(), is("Book4"));
        assertThat(books.get(2).getName(), is("Book3"));
        assertThat(books.get(3).getName(), is("Book2"));
        assertThat(books.get(4).getName(), is("Book1"));
    }

    private List<Notebook> createTags() {
        List<Notebook> books = new ArrayList<>(5);
        books.add(create("Book3"));
        books.add(create("Book1"));
        books.add(create("ノートブック5"));
        books.add(create("Book4"));
        books.add(create("Book2"));

        return books;
    }

    private Notebook create(String name) {
        Notebook tag = new Notebook();
        tag.setName(name);
        return tag;
    }
}
