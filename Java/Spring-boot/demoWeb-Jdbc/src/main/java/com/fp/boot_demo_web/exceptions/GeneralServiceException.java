package com.fp.boot_demo_web.exceptions;

/**
 * Indicates that a service operation could not be completed.
 */
public class GeneralServiceException extends RuntimeException {

    /** Creates the standard service-operation failure exception. */
    public GeneralServiceException() {
        super("Runtime exception generated during service execution");
    }

}
