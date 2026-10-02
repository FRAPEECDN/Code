package com.fp.coding;

import lombok.Getter;

/** Department staff happiness ratings and their display labels. */
@Getter
public enum StaffContent {
    HAPPY("happy"),
    NEUTRAL("neutral"),
    SAD("sad");

    private final String value;

    StaffContent(String value) {
        this.value = value;
    }
}
