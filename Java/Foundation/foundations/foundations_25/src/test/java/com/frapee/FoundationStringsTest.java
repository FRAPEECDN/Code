package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class FoundationStringsTest {

    private final FoundationStrings strings = new FoundationStrings();

    @Test
    public void testEqualityAndImmutabilityBehavior() {
        String original = "hello";
        String result = strings.concatenate(original, " world");

        assertThat(result, equalTo("hello world"));
        assertThat(original, equalTo("hello"));
        assertTrue(strings.equalByValue(new String("same"), new String("same")));
        assertFalse(strings.equalByValue("same", "different"));
    }

    @Test
    public void testStringBuilderAndCommonOperations() {
        assertThat(strings.buildText("item", "value", 5), equalTo("item:value:5"));
        assertThat(strings.lengthOf("hello"), equalTo(5));
        assertThat(strings.characterAt("hello", 1), equalTo('e'));
        assertTrue(strings.contains("hello world", "world"));
        assertThat(strings.substring("hello world", 0, 5), equalTo("hello"));
        assertThat(strings.replace("one two", "two", "three"), equalTo("one three"));
        assertThat(strings.trimAndUppercase("  hello  "), equalTo("HELLO"));
    }

    @Test
    public void testSplitAndTextBlocks() {
        assertThat(Arrays.asList(strings.split("one,two,three", ",")),
            equalTo(Arrays.asList("one", "two", "three")));
        assertThat(strings.textBlock(), equalTo("first line\nsecond line\n"));
    }
}