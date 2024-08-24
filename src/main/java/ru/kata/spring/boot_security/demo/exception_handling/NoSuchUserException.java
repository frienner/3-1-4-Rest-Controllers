package ru.kata.spring.boot_security.demo.exception_handling;

public class NoSuchUserException extends RuntimeException {
    public NoSuchUserException(String message) {

        super("There is no user with username: " + message);
    }
}
