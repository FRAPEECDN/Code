package com.fp.coding;

/**
 * Immutable counterpart to {@link PojoInformation}, implemented as a Java record.
 *
 * <p>The compact canonical constructor validates and normalizes components before
 * the record stores them; generated accessors and value methods keep the example concise.
 *
 * @param name non-blank personal name; surrounding whitespace is removed
 * @param age positive age
 * @param salary finite, non-negative salary
 */
public record RecordInformation(String name, int age, double salary) implements Information {
    /**
     * Validates and normalizes the record components during construction.
     *
     * @throws IllegalArgumentException if a component violates its documented constraint
     */
    public RecordInformation {
        name = NameValidation.normalize(name);
        if (age <= 0) {
            throw new IllegalArgumentException("age must be greater than 0");
        }
        if (!Double.isFinite(salary) || salary < 0) {
            throw new IllegalArgumentException("salary must be finite and 0 or above");
        }
    }

    @Override
    public String summary() {
        return "RecordInformation[name='%s', age=%d, salary=%.2f]".formatted(name, age, salary);
    }
}