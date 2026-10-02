package com.frapee;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FoundationEncodingStringsTest {

    @Test
    public void testEncodingLower() {
        String testString = "abcdefghijklm nopqrstuvwxyz";
        String expectedString = "1 2 3 4 5 6 7 8 9 10 11 12 13+14 15 16 17 18 19 20 21 22 23 24 25 26";
        String actualString = FoundationEncodingStrings.encodeToNumberLowerAlpha(testString);
        assertThat(actualString, equalTo(expectedString));        
    }

    @Test
    public void testDecodingLower() {
        String testString = "1 2 3 4 5 6 7 8 9 10 11 12 13+14 15 16 17 18 19 20 21 22 23 24 25 26";
        String expectedString = "abcdefghijklm nopqrstuvwxyz";
        String actualString = FoundationEncodingStrings.decodeToLowerAlphaNumber(testString);
        assertThat(actualString, equalTo(expectedString));
    }

    @Test
    public void testEncodingUpper() {
        String testString = "ABCDEFGHIJKLM NOPQRSTUVWXYZ";
        String expectedString = "1 2 3 4 5 6 7 8 9 10 11 12 13+14 15 16 17 18 19 20 21 22 23 24 25 26";
        String actualString = FoundationEncodingStrings.encodeToNumberUpperAlpha(testString);
        assertThat(actualString, equalTo(expectedString));
    }

    @Test
    public void testDecodingUpper() {
        String testString = "1 2 3 4 5 6 7 8 9 10 11 12 13+14 15 16 17 18 19 20 21 22 23 24 25 26";
        String expectedString = "ABCDEFGHIJKLM NOPQRSTUVWXYZ";
        String actualString = FoundationEncodingStrings.decodeToUpperAlphaNumber(testString);
        assertThat(actualString, equalTo(expectedString));
    }

    @Test
    public void testEncodeAlphaNumeric() {
        String testString = "AaBbCcDdEeFfGgHhIiJjKkLlMm []{}NnOoPpQqRrSsTtUuVvWwXxYyZz";
        String expectedString = "34 66 35 67 36 68 37 69 38 70 39 71 40 72 41 73 42 74 43 75 44 76 45 77 46 78 1 60 62 92 94 47 79 48 80 49 81 50 82 51 83 52 84 53 85 54 86 55 87 56 88 57 89 58 90 59 91";
        String actualString = FoundationEncodingStrings.encodeAlphaNumeric(testString);
        assertThat(actualString, equalTo(expectedString));
    }

    @Test
    public void testDecodeAlphaNumeric() {
        String testString = "34 66 35 67 36 68 37 69 38 70 39 71 40 72 41 73 42 74 43 75 44 76 45 77 46 78 1 60 62 92 94 47 79 48 80 49 81 50 82 51 83 52 84 53 85 54 86 55 87 56 88 57 89 58 90 59 91";
        String expectedString = "AaBbCcDdEeFfGgHhIiJjKkLlMm []{}NnOoPpQqRrSsTtUuVvWwXxYyZz";
        String actualString = FoundationEncodingStrings.decodeAlphaNumeric(testString);
        assertThat(actualString, equalTo(expectedString));
    }

}
