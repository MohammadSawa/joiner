package io.appswave.joiner.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("auth.user.notfound");
    }
}

