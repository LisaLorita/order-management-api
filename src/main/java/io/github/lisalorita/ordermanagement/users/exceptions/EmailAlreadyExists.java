package io.github.lisalorita.ordermanagement.users.exceptions;

public class EmailAlreadyExists extends RuntimeException {
    public EmailAlreadyExists(String email) {
        super("Email " + email + " already in use");
    }
}