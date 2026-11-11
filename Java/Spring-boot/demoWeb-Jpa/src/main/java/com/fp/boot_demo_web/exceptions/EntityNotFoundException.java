package com.fp.boot_demo_web.exceptions;

/**
 * Indicates that a requested record does not exist.
 */
public class EntityNotFoundException extends RuntimeException {

    /** Creates the standard record-not-found exception. */
    public EntityNotFoundException() {
        super("Entity was not found");
    }

}
