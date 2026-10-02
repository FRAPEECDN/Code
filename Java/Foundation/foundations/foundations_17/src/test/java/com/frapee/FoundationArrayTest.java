package com.frapee;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

/**
 * Unit testing for Foundation using Arrays.
 */
public class FoundationArrayTest {

    /**
     * Compare function using predefined Arrays class
     * @param actual - actual results from test
     * @param expected - expected values for the test
     */
    private void compareResultsWithArrays(int[] actual, int[] expected) {
        assertThat(Arrays.compare(actual, expected), equalTo(0));
    }

    /**
     * Custom compare function
     * @param actual - actual results from test
     * @param expected - expected values for the test
     */
    private void compareResultWithLoop(int[] actual, int[] expected) {
        assertThat(actual.length, equalTo(expected.length));
        for (int i = 0; i < actual.length; i++) {
            assertThat(actual[i], equalTo(expected[i]));
        }
    }

    @Test
    public void testSortedArrayMerge() {
        FoundationArray fa = new FoundationArray();
        int[] leftArray = {3, 5, 7, 9};
        int[] rightArray = {1, 2, 4, 6, 8};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        int[] result = fa.mergeSortedArrays(leftArray, rightArray);
        compareResultWithLoop(result, expected);
    }

    @Test
    public void testUnsortedArrayMerge() {
        FoundationArray fa = new FoundationArray();
        int[] leftArray = {8, 6, 4, 9};
        int[] rightArray = {7, 3, 5, 2, 1};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        int[] result = fa.mergeAndSortArrays(leftArray, rightArray);
        compareResultWithLoop(result, expected);
    }

    @Test
    public void testExistinMethods() {
        FoundationArray fa = new FoundationArray();
        int[] leftArray = {8, 6, 4, 9};
        int[] rightArray = {7, 3, 5, 2, 1};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9};

        int[] result = fa.mergeAndSortUsingExisting(leftArray, rightArray);
        compareResultsWithArrays(result, expected);
    }

    @Test
    public void testBinarySearchFound() {
        int[] sortedArray = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int position = Arrays.binarySearch(sortedArray, 3);
        assertThat(position, equalTo(2));
        position = Arrays.binarySearch(sortedArray, 5);
        assertThat(position, equalTo(4));
        position = Arrays.binarySearch(sortedArray, 7);
        assertThat(position, equalTo(6));
    }

    @Test
    public void testBinarySearchNotFound() {
        int[] sortedArray = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int position = Arrays.binarySearch(sortedArray, 10);
        assertThat(position, lessThan(0));        
    }

    @Test
    public void testBinarySearchFoundCustom() {
        FoundationArray fa = new FoundationArray();
        int[] sortedArray = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int position = fa.findSortedArrayTarget(sortedArray, 3);
        assertThat(position, equalTo(2));
        position = fa.findSortedArrayTarget(sortedArray, 5);
        assertThat(position, equalTo(4));
        position = fa.findSortedArrayTarget(sortedArray, 7);
        assertThat(position, equalTo(6));
    }

    @Test
    public void testBinarySearchNotFoundCustom() {
        FoundationArray fa = new FoundationArray();
        int[] sortedArray = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int position = fa.findSortedArrayTarget(sortedArray, 10);
        assertThat(position, equalTo(-1));
    }

    @Test
    public void testCustomSort() {
        FoundationArray fa = new FoundationArray();
        int[] unsortedArray = {2, 9, 4, 7, 3, 6, 5, 8, 1};
        int[] sortedArray = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        fa.sortArray(unsortedArray);
        compareResultsWithArrays(unsortedArray, sortedArray);
    }     

    @Test
    public void testLinearSearchFound() {
        FoundationArray fa = new FoundationArray();
        int[] unsortedArray = {2, 9, 4, 7, 3, 6, 5, 8, 1};
        int position = fa.findUnSortedArrayTarget(unsortedArray, 3);
        assertThat(position, equalTo(4));
        position = fa.findUnSortedArrayTarget(unsortedArray, 5);
        assertThat(position, equalTo(6));
        position = fa.findUnSortedArrayTarget(unsortedArray, 7);
        assertThat(position, equalTo(3));
    }
    
    @Test
    public void testLinearSearchNotFound() {
        FoundationArray fa = new FoundationArray();
        int[] unsortedArray = {2, 9, 4, 7, 3, 6, 5, 8, 1};
        int position = fa.findUnSortedArrayTarget(unsortedArray, 10);
        assertThat(position, equalTo(-1));
    }

    @Test
    public void testStreamMatch() {
        FoundationArray fa = new FoundationArray();
        int[] matchingArray = {2, 4, 6, 8, 10};
        int[] noMatchingArray = {1, 2, 4, 6, 8, 10};

        assertTrue(fa.predicateArrayFindEven(matchingArray));
        assertFalse(fa.predicateArrayFindEven(noMatchingArray));
    }

    @Test
    public void testStreamFilter() {
        FoundationArray fa = new FoundationArray();
        int[] lookupArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] expectedArray = {2, 4, 6, 8, 10};

        int[] result = fa.arrayStreamFilter(lookupArray);
        compareResultsWithArrays(result, expectedArray);
    }

    @Test
    public void testArraysEqualsAndToString() {
        FoundationArray fa = new FoundationArray();
        int[] firstArray = {1, 2, 3};
        int[] sameArray = {1, 2, 3};
        int[] differentArray = {1, 3, 2};

        assertTrue(fa.arraysEquals(firstArray, sameArray));
        assertFalse(fa.arraysEquals(firstArray, differentArray));
        assertThat(fa.arraysToString(firstArray), equalTo("[1, 2, 3]"));
    }

    @Test
    public void testArraysCopyOperations() {
        FoundationArray fa = new FoundationArray();
        int[] inputArray = {1, 2, 3};

        compareResultsWithArrays(fa.arraysCopyOf(inputArray, 5), new int[] {1, 2, 3, 0, 0});
        compareResultsWithArrays(fa.arraysCopyOfRange(inputArray, 1, 3), new int[] {2, 3});
    }

    @Test
    public void testArraysFillMismatchAndSetAll() {
        FoundationArray fa = new FoundationArray();
        int[] filledArray = {1, 2, 3};
        fa.arraysFill(filledArray, 7);
        compareResultsWithArrays(filledArray, new int[] {7, 7, 7});

        assertThat(fa.arraysMismatch(new int[] {1, 2, 3}, new int[] {1, 4, 3}), equalTo(1));
        assertThat(fa.arraysMismatch(new int[] {1, 2, 3}, new int[] {1, 2, 3}), equalTo(-1));

        int[] generatedArray = new int[4];
        fa.arraysSetAll(generatedArray);
        compareResultsWithArrays(generatedArray, new int[] {0, -1, -2, -3});
    }

    @Test
    public void testArraysParallelSort() {
        FoundationArray fa = new FoundationArray();
        int[] inputArray = {4, 1, 3, 2};

        fa.arraysParallelSort(inputArray);

        compareResultsWithArrays(inputArray, new int[] {1, 2, 3, 4});
    }

}
