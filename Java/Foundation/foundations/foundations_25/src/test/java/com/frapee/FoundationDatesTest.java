package com.frapee;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

import org.junit.jupiter.api.Test;

public class FoundationDatesTest {

    private final FoundationDates dates = new FoundationDates();

    @Test
    public void testDateAndPeriodOperations() {
        LocalDate start = dates.createDate(2026, 1, 1);
        assertThat(dates.addDays(start, 10), equalTo(LocalDate.of(2026, 1, 11)));
        assertThat(dates.dateDifference(start, LocalDate.of(2026, 2, 1)), equalTo(Period.ofMonths(1)));
        assertThat(dates.formatDate(start, "yyyy-MM-dd"), equalTo("2026-01-01"));
        assertThat(dates.parseDate("2026/01/01", "yyyy/MM/dd"), equalTo(start));
    }

    @Test
    public void testDateTimeAndDurationOperations() {
        LocalDateTime start = dates.createDateTime(LocalDate.of(2026, 1, 1), 10, 0);
        LocalDateTime end = start.plusHours(2);
        assertThat(dates.timeDifference(start, end), equalTo(Duration.ofHours(2)));
    }
}