package ru.kata.spring.boot_security.demo.exception_handling;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserIncorrectData {
    private String message;

    public UserIncorrectData() {}

}
