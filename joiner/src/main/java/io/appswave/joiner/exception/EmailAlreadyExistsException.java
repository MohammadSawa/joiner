package io.appswave.joiner.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("user.exists");
    }
}
