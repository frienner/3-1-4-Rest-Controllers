package ru.kata.spring.boot_security.demo.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // Создаем роли
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");

        // Создаем пользователя и добавляем роли
        user = new User();
        user.setUsername("test@example.com");
        user.setPassword("password");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setAge(30);
        user.setRoles(Arrays.asList(role1, role2)); // Добавляем роли
    }

    @Test
    void setUsername_incorrectUsername_throwsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> user.setUsername("invalidUsername"));
    }

    @Test
    void setUsername_incorrectUsername_sameUsername() {
        String username = user.getUsername();
        try {
            user.setUsername("invalidUsername");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals(username, user.getUsername());
        }
    }

    @Test
    void setAge_belowZero_throwsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> user.setAge(-1));
    }

    @Test
    void setAge_belowZero_sameAge() {
        int age = user.getAge();
        try {
            user.setAge(-1);
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals(age, user.getAge());
        }
    }

    @Test
    void setAge_tooOld_throwsException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> user.setAge(121));
    }

    @Test
    void setAge_tooOld_sameAge() {
        int age = user.getAge();
        try {
            user.setAge(121);
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals(age, user.getAge());
        }
    }

    @Test
    void setAge_correctAge_newAge() {
        int age = 50;
        user.setAge(age);
        Assertions.assertEquals(age, user.getAge());
    }

}