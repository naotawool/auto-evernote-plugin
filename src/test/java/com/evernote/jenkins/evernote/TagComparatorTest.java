package com.evernote.jenkins.evernote;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.evernote.edam.type.Tag;

/**
 * {@link TagComparator}に対するテストクラス。
 *
 * @author naotake
 */
public class TagComparatorTest {

    @Test
    public void タグ名の昇順に並び替えられること() {
        List<Tag> tags = createTags();
        Collections.sort(tags, TagComparator.ASC);

        // 検証
        assertThat(tags.get(0).getName(), is("Tag1"));
        assertThat(tags.get(1).getName(), is("Tag2"));
        assertThat(tags.get(2).getName(), is("Tag3"));
        assertThat(tags.get(3).getName(), is("Tag4"));
        assertThat(tags.get(4).getName(), is("タグ5"));
    }

    @Test
    public void タグ名の降順に並び替えられること() {
        List<Tag> tags = createTags();
        Collections.sort(tags, TagComparator.DESC);

        // 検証
        assertThat(tags.get(0).getName(), is("タグ5"));
        assertThat(tags.get(1).getName(), is("Tag4"));
        assertThat(tags.get(2).getName(), is("Tag3"));
        assertThat(tags.get(3).getName(), is("Tag2"));
        assertThat(tags.get(4).getName(), is("Tag1"));
    }

    private List<Tag> createTags() {
        List<Tag> tags = new ArrayList<>(5);
        tags.add(create("Tag3"));
        tags.add(create("Tag1"));
        tags.add(create("タグ5"));
        tags.add(create("Tag4"));
        tags.add(create("Tag2"));

        return tags;
    }

    private Tag create(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        return tag;
    }
}
