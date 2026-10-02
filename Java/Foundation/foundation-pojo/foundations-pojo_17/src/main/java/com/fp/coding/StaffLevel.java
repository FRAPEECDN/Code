package com.fp.coding;

import lombok.Getter;

/** Department developer position and its numeric rank. */
@Getter
public enum StaffLevel {
    JUNIOR(1),
    MID(2),
    SENIOR(3),
    LEAD(4);

    private final int staffLevel;

    StaffLevel(int staffLevel) {
        this.staffLevel = staffLevel;
    }
}
