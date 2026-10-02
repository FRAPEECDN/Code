package com.fp.coding;

import java.io.Serial;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

/**
 * Mutable UUID-identified bean demonstrating common Java object contracts.
 *
 * <p>This example implements the project {@link Information} interface,
 * {@link Comparable} for deterministic natural ordering, {@link Serializable}
 * for Java object streams, and {@link Cloneable} for an independent same-identity
 * copy. The natural order is case-insensitive by name, then uses exact name, age,
 * salary, and UUID as tie-breakers; it is consistent with {@link #equals(Object)}.
 *
 * <p>Serialization writes the non-static instance state, including the UUID.
 * {@code serialVersionUID} identifies this class's serialized form. Java native
 * deserialization bypasses constructors, so object streams should only be read
 * from trusted sources. Equality and hashing include mutable value fields; do not
 * change those fields while this object is stored in a hash-based collection.
 */
public final class UuidPojoInformation
        implements Information, Comparable<UuidPojoInformation>, Serializable, Cloneable {
    /** Version identifier for this class's Java serialization form. */
    @Serial
    private static final long serialVersionUID = 1L;

    /** Natural ordering: case-insensitive name, exact name, age, salary, then UUID. */
    private static final Comparator<UuidPojoInformation> NATURAL_ORDER =
            Comparator.comparing(UuidPojoInformation::getName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(UuidPojoInformation::getName)
                    .thenComparingInt(UuidPojoInformation::getAge)
                    .thenComparing(UuidPojoInformation::getSalary,
                            Comparator.nullsFirst(Comparator.naturalOrder()))
                    .thenComparing(UuidPojoInformation::getId);

    /** Alternate ordering by age, then by the natural name-based ordering. */
    public static final Comparator<UuidPojoInformation> BY_AGE =
            Comparator.comparingInt(UuidPojoInformation::getAge).thenComparing(NATURAL_ORDER);

    private final UUID id;
    private String name;
    private int age;
    private Double salary;

    /** Creates an example bean with a generated UUID and placeholder values. */
    public UuidPojoInformation() {
        this(UUID.randomUUID(), "Unknown", 1, null);
    }

    /**
     * Creates a bean with a newly generated UUID.
     *
     * @param name valid personal name
     * @param age positive age
     * @param salary finite non-negative salary, or null when unspecified
     */
    public UuidPojoInformation(String name, int age, Double salary) {
        this(UUID.randomUUID(), name, age, salary);
    }

    /**
     * Creates a bean with an explicit UUID, useful when restoring stored identity.
     *
     * @param id non-null identifier
     * @param name valid personal name
     * @param age positive age
     * @param salary finite non-negative salary, or null when unspecified
     * @throws NullPointerException if {@code id} is null
     * @throws IllegalArgumentException if a name, age, or salary is invalid
     */
    public UuidPojoInformation(UUID id, String name, int age, Double salary) {
        this.id = Objects.requireNonNull(id, "id");
        setName(name);
        setAge(age);
        setSalary(salary);
    }

    /** @return the immutable UUID identifying this bean */
    public UUID getId() {
        return id;
    }

    /** @return the normalized name */
    public String getName() {
        return name;
    }

    /**
     * Replaces this bean's name after validation and normalization.
     *
     * @param name valid personal name; surrounding whitespace is removed
     * @throws IllegalArgumentException if the name is null or invalid
     */
    public void setName(String name) {
        this.name = NameValidation.normalize(name);
    }

    /** @return the positive age */
    public int getAge() {
        return age;
    }

    /**
     * Sets the age.
     *
     * @param age positive age
     * @throws IllegalArgumentException if {@code age} is not positive
     */
    public void setAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("age must be greater than 0");
        }
        this.age = age;
    }

    /** @return the optional salary, or null when it is unspecified */
    public Double getSalary() {
        return salary;
    }

    /**
     * Sets the optional salary.
     *
     * @param salary finite non-negative salary, or null when unspecified
     * @throws IllegalArgumentException if a non-null salary is negative or non-finite
     */
    public void setSalary(Double salary) {
        if (salary != null && (!Double.isFinite(salary) || salary < 0)) {
            throw new IllegalArgumentException("salary must be finite and 0 or above");
        }
        this.salary = salary;
    }

    @Override
    public String summary() {
        return "UuidPojoInformation[id=%s, name='%s', age=%d, salary=%s]"
                .formatted(id, name, age, salary);
    }

    /**
    * Compares beans by case-insensitive name, exact name, age, salary, then UUID.
     *
     * @param other bean to compare with
     * @return a negative value, zero, or a positive value as this bean sorts before,
    *         equal to, or after {@code other}; zero exactly when {@link #equals(Object)} is true
     * @throws NullPointerException if {@code other} is null
     */
    @Override
    public int compareTo(UuidPojoInformation other) {
        return NATURAL_ORDER.compare(this, Objects.requireNonNull(other, "other"));
    }

    /**
     * Creates a new bean with the same name, age, and salary but a newly generated UUID.
     *
     * <p>The new bean represents a different identity and therefore is not equal to
     * this bean, even though its non-identity values match.
     *
     * @return a bean with copied values and a new identifier
     */
    public UuidPojoInformation copyWithNewId() {
        return new UuidPojoInformation(name, age, salary);
    }

    /**
     * Creates an independent copy retaining this bean's UUID and current values.
     *
     * <p>This class has no mutable object-valued fields: {@link UUID}, {@link String},
     * and {@link Double} are immutable, while age is primitive. Copying those values
     * into a new bean therefore produces a deep copy of the current object graph.
     * If a mutable field is added later, this method must copy that field as well.
     *
     * @return a separate bean with the same identity and values
     */
    public UuidPojoInformation deepCopy() {
        return new UuidPojoInformation(id, name, age, salary);
    }

    /**
     * Creates a copy retaining this bean's UUID and current values.
     *
     * <p>{@link Cloneable} is only a marker; this override performs the copy by
     * constructing a new instance rather than calling {@link Object#clone()}.
     * It delegates to {@link #deepCopy()} because all referenced field values are
     * immutable.
     *
     * @return an independent copy representing the same UUID-identified entity
     */
    @Override
    public UuidPojoInformation clone() {
        return deepCopy();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, salary);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UuidPojoInformation other)) {
            return false;
        }
        return age == other.age
                && id.equals(other.id)
                && Objects.equals(name, other.name)
                && Objects.equals(salary, other.salary);
    }

    @Override
    public String toString() {
        return summary();
    }
}