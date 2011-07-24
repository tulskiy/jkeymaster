package com.tulskiy.keymaster;

/**
 * Could be thrown on error during key registration process.
 * Author Alex (mq0) Ivanov  at 24.07.11 15:21
 */
public class RegistrationException extends JKeyMasterException {
    public RegistrationException() {
        super();
    }

    public RegistrationException(String message) {
        super(message);
    }

    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RegistrationException(Throwable cause) {
        super(cause);
    }
}
