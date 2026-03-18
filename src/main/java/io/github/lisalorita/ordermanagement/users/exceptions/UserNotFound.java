package io.github.lisalorita.ordermanagement.users.exceptions;

import java.util.UUID;

public class UserNotFound extends RuntimeException {
    public UserNotFound(UUID id) {
        super("The user with id " + id + " was not found.");
    }
}
