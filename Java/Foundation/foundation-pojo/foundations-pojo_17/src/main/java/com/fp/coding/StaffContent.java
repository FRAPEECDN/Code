package com.fp.coding;

import lombok.Getter;

/** Department staff happiness ratings and their display labels. */
@Getter
public enum StaffContent {
    /** Positive content with display value {@code "happy"}. */
    HAPPY("happy"),
    /** Neutral content with display value {@code "neutral"}. */
    NEUTRAL("neutral"),
    /** Negative content with display value {@code "sad"}. */
    SAD("sad");

    /** Text label used when displaying this state. */
    private final String value;

    StaffContent(String value) {
        this.value = value;
    }
}
