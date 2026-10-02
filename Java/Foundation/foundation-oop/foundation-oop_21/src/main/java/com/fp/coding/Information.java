package com.fp.coding;

/**
 * Common contract for the information objects used in the OOP and generics examples.
 *
 * <p>This sealed interface makes the set of direct implementations explicit, allowing
 * the Java 21 record-pattern switch to handle each supported kind exhaustively.
 */
public sealed interface Information permits PojoInformation, RecordInformation, UuidPojoInformation, Staff {
    /**
     * Returns a concise, human-readable description of this information object.
     *
     * @return a summary of this object's identifying data
     */
    String summary();
}