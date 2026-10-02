package com.fp.coding;

/** Runnable section comparing mutable POJO state with immutable record values. */
public final class ModelExamples {
    private ModelExamples() {
    }

    /** Runs the POJO and record model examples. */
    public static void main(String[] args) {
        PojoInformation bean = new PojoInformation(1, "Ada Lovelace", 36, 120000.0);
        RecordInformation record = new RecordInformation("Grace Hopper", 85, 150000.0);

        System.out.println("POJO model:");
        System.out.println(bean);
        bean.setAge(37);
        System.out.println("POJO after setter mutation: " + bean.summary());

        System.out.println("Record model:");
        System.out.println(record);
        System.out.println("Record value equality: "
                + record.equals(new RecordInformation("Grace Hopper", 85, 150000.0)));
    }
}