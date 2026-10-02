package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class FoundationBoxingTest {

    private final FoundationBoxing boxing = new FoundationBoxing();

    @Test
    public void testBoxingAndUnboxing() {
        assertThat(boxing.boxBoolean(true), equalTo(Boolean.TRUE));
        assertThat(boxing.boxByte((byte) 1), equalTo(Byte.valueOf((byte) 1)));
        assertThat(boxing.boxShort((short) 2), equalTo(Short.valueOf((short) 2)));
        assertThat(boxing.boxInt(3), equalTo(Integer.valueOf(3)));
        assertThat(boxing.boxLong(4L), equalTo(Long.valueOf(4L)));
        assertThat(boxing.boxFloat(5.0f), equalTo(Float.valueOf(5.0f)));
        assertThat(boxing.boxDouble(6.0), equalTo(Double.valueOf(6.0)));
        assertThat(boxing.boxChar('a'), equalTo(Character.valueOf('a')));
        assertThat(boxing.unboxInt(Integer.valueOf(7)), equalTo(7));
    }

    @Test
    public void testWrapperFactoriesParsingAndEquality() {
        assertThat(boxing.valueOfInt(8), equalTo(Integer.valueOf(8)));
        assertThat(boxing.parseInt("9"), equalTo(9));
        assertThat(boxing.equalIntegerValues(1000, 1000), equalTo(true));
        assertThat(boxing.demonstrateIntegerCaching(), equalTo(true));
    }

    @Test
    public void testNullUnboxing() {
        assertThrows(NullPointerException.class, boxing::unboxNull);
    }
}