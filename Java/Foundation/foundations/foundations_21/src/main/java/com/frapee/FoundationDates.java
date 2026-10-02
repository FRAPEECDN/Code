package com.frapee;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * Foundation guide to java.time date and time types.
 * LocalDate represents dates, LocalDateTime combines date and time, Period
 * measures calendar units, and Duration measures elapsed time.
 */
public class FoundationDates {

    /**
     * Create an immutable calendar date.
     * @param year calendar year
     * @param month month from 1 to 12
     * @param day day of month
     * @return immutable LocalDate
     */
    public LocalDate createDate(int year, int month, int day) {
        return LocalDate.of(year, month, day);
    }

    /**
     * Add whole calendar days.
     * @param date starting date
     * @param days number of days to add
     * @return adjusted immutable date
     */
    public LocalDate addDays(LocalDate date, long days) {
        return date.plusDays(days);
    }

    /**
     * Calculate a calendar-based difference.
     * @param start beginning date
     * @param end ending date
     * @return difference as a Period
     */
    public Period dateDifference(LocalDate start, LocalDate end) {
        return Period.between(start, end);
    }

    /**
     * Combine a date with a time.
     * @param date date portion
     * @param hour hour of day
     * @param minute minute of hour
     * @return combined LocalDateTime
     */
    public LocalDateTime createDateTime(LocalDate date, int hour, int minute) {
        return date.atTime(hour, minute);
    }

    /**
     * Calculate elapsed time between two date-times.
     * @param start beginning date-time
     * @param end ending date-time
     * @return elapsed difference as a Duration
     */
    public Duration timeDifference(LocalDateTime start, LocalDateTime end) {
        return Duration.between(start, end);
    }

    /**
     * Format a date with a DateTimeFormatter pattern.
     * @param date date to format
     * @param pattern formatter pattern
     * @return formatted date text
     */
    public String formatDate(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Parse date text with a DateTimeFormatter pattern.
     * @param value date text
     * @param pattern formatter pattern
     * @return parsed immutable date
     */
    public LocalDate parseDate(String value, String pattern) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
    }
}
