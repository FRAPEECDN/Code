package com.fp.coding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Has-a example that groups mutable POJOs and immutable record snapshots.
 *
 * <p>The catalog encapsulates two collections, demonstrating object composition
 * through collaborating objects. It keeps the supplied POJO references, so changes
 * to a stored POJO remain visible through the catalog. Lookup methods return
 * {@link Optional} values instead of {@code null} when an entry is absent.
 */
public final class InformationCatalog {
    private final List<PojoInformation> pojos = new ArrayList<>();
    private final List<RecordInformation> records = new ArrayList<>();

    /** Creates an empty catalog. */
    public InformationCatalog() {
    }

    /**
     * Adds a mutable bean to the catalog.
     *
     * @param pojo bean to store
     * @throws NullPointerException if {@code pojo} is null
     */
    public void add(PojoInformation pojo) {
        pojos.add(Objects.requireNonNull(pojo, "pojo"));
    }

    /**
     * Adds an immutable record to the catalog.
     *
     * @param record record to store
     * @throws NullPointerException if {@code record} is null
     */
    public void add(RecordInformation record) {
        records.add(Objects.requireNonNull(record, "record"));
    }

    /**
     * Finds the first bean with the requested identifier.
     *
     * @param id identifier to find
     * @return the matching bean, or an empty optional if no bean has that identifier
     */
    public Optional<PojoInformation> findPojoById(long id) {
        return pojos.stream().filter(pojo -> pojo.getId() == id).findFirst();
    }

    /**
     * Finds the first record whose name equals the supplied name.
     *
     * @param name name to find; must not be null
     * @return the matching record, or an empty optional if no record has that name
     * @throws NullPointerException if {@code name} is null
     */
    public Optional<RecordInformation> findRecordByName(String name) {
        Objects.requireNonNull(name, "name");
        return records.stream().filter(record -> Objects.equals(record.name(), name)).findFirst();
    }

    /**
     * Finds a bean's optional salary.
     *
     * <p>{@link Optional#map(java.util.function.Function)} converts a null salary
     * returned by the bean into an empty optional as well.
     *
     * @param id identifier of the bean
     * @return the salary when both the bean and its salary are present; otherwise empty
     */
    public Optional<Double> findPojoSalary(long id) {
        return findPojoById(id).map(PojoInformation::getSalary);
    }

    /**
     * Returns an immutable list of all entries through their shared interface.
     *
     * @return a snapshot containing the stored beans followed by the stored records
     */
    public List<Information> entries() {
        ArrayList<Information> result = new ArrayList<>(pojos);
        result.addAll(records);
        return List.copyOf(result);
    }
}