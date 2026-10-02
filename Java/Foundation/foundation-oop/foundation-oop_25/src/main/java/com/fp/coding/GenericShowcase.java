package com.fp.coding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Small mutable collection used to teach generic types and wildcard variance.
 *
 * <p>{@code T} is the stored element type. The producer method accepts
 * {@code Collection<? extends T>}, while {@link #copyTo(Collection)} accepts
 * {@code Collection<? super T>}; these are the producer-extends and
 * consumer-super patterns. Static methods additionally demonstrate unbounded
 * wildcards, bounded type parameters, and generic methods.
 *
 * @param <T> type of values stored by this showcase
 */
public final class GenericShowcase<T> {
    private final ArrayList<T> items = new ArrayList<>();

    /** Creates an empty showcase for values of type {@code T}. */
    public GenericShowcase() {
    }

    /**
     * Adds one non-null value.
     *
     * @param item value to store
     * @throws NullPointerException if {@code item} is null
     */
    public void add(T item) {
        items.add(Objects.requireNonNull(item, "item"));
    }

    /**
     * Adds values from a collection whose element type is {@code T} or a subtype.
     *
     * @param source producer collection of values
     * @throws NullPointerException if the collection or one of its elements is null
     */
    public void addAll(Collection<? extends T> source) {
        source.forEach(this::add);
    }

    /**
     * Returns an immutable snapshot of the stored values.
     *
     * @return an unmodifiable list copy
     */
    public List<T> items() {
        return List.copyOf(items);
    }

    /**
     * Copies the stored values into a consumer collection of {@code T} or a supertype.
     *
     * @param destination collection that can accept values of type {@code T}
     */
    public void copyTo(Collection<? super T> destination) {
        destination.addAll(items);
    }

    /**
     * Prints a heading and every stored value.
     *
     * @param title heading to print before the values
     */
    public void display(String title) {
        displayWildcard(title, items);
    }

    /**
     * Prints any collection without knowing its element type, using {@code ?}.
     *
     * @param title heading to print before the values
     * @param values collection of any reference type
     */
    public static void displayWildcard(String title, Collection<?> values) {
        System.out.println(title);
        values.forEach(System.out::println);
    }

    /**
     * Displays summaries from values bounded by the {@link Information} contract.
     *
     * @param <T> information type represented in the input collection
     * @param values producer collection of information values
     */
    public static <T extends Information> void displaySummaries(Collection<? extends T> values) {
        values.forEach(value -> System.out.println(value.summary()));
    }

    /**
     * Finds the greatest comparable value, illustrating a bounded generic method.
     *
     * @param <T> comparable element type
     * @param values non-empty producer collection of comparable values
     * @return the greatest element according to its natural ordering
     * @throws IllegalArgumentException if {@code values} is empty
     */
    public static <T extends Comparable<? super T>> T largest(Collection<? extends T> values) {
        return values.stream().max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("values must not be empty"));
    }
}