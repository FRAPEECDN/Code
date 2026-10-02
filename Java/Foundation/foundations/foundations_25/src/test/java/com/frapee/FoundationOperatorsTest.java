package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

public class FoundationOperatorsTest {

    private final FoundationOperators operators = new FoundationOperators();

    @Test
    public void testBitwiseAndShiftOperators() {
        assertThat(operators.bitwiseAnd(6, 3), equalTo(2));
        assertThat(operators.bitwiseOr(6, 3), equalTo(7));
        assertThat(operators.bitwiseXor(6, 3), equalTo(5));
        assertThat(operators.bitwiseNot(0), equalTo(-1));
        assertThat(operators.leftShift(1, 3), equalTo(8));
        assertThat(operators.signedRightShift(-8, 1), equalTo(-4));
        assertThat(operators.unsignedRightShift(-8, 31), equalTo(1));
    }

    @Test
    public void testCompoundAssignmentAndPrecedence() {
        assertThat(operators.compoundAdd(5, 3), equalTo(8));
        assertThat(operators.withoutParentheses(), equalTo(14));
        assertThat(operators.withParentheses(), equalTo(20));
    }

    @Test
    public void testShortCircuitEvaluation() {
        assertThat(operators.shortCircuitAnd(false, 0), equalTo(false));
        assertThat(operators.shortCircuitOr(true, 0), equalTo(true));
        assertThat(operators.shortCircuitAnd(true, 2), equalTo(true));
        assertThat(operators.shortCircuitOr(false, 2), equalTo(true));
    }
}