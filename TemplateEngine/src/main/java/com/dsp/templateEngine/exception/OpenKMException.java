package com.dsp.templateEngine.exception;
public class OpenKMException extends RuntimeException {
    public OpenKMException(String message) {
        super(message);
    }

    public OpenKMException(String message, Throwable cause) {
        super(message, cause);
    }
}
