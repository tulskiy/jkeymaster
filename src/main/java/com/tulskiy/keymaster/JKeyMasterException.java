package com.tulskiy.keymaster;

/**
 * Root JkeyMasterException.
 * Author Alex (mq0) Ivanov at 24.07.11 15:23
 */
public class JKeyMasterException extends RuntimeException {
    public JKeyMasterException() {
        super();
    }

    public JKeyMasterException(String message) {
        super(message);
    }

    public JKeyMasterException(String message, Throwable cause) {
        super(message, cause);
    }

    public JKeyMasterException(Throwable cause) {
        super(cause);
    }
}
