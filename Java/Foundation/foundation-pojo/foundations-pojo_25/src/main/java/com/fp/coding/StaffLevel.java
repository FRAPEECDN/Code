package com.fp.coding;

import lombok.Getter;

/** Department developer position and its numeric rank. */
@Getter
public enum StaffLevel {
    /** Entry-level developer with rank 1. */
    JUNIOR(1),
    /** Intermediate developer with rank 2. */
    MID(2),
    /** Experienced developer with rank 3. */
    SENIOR(3),
    /** Lead developer with rank 4. */
    LEAD(4);

    /** Numeric rank used for ordering staff levels. */
    private final int ordinal;

    StaffLevel(int ordinal) {
        this.ordinal = ordinal;
    }
}
