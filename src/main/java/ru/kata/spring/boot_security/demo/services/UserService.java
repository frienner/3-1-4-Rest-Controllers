package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService extends UserDetailsService {

    User findByUsername(String username);

    User findById(long id);

    List<User> findAll();

    List<User> findAllOrderedById();

    User save(User user);

    void delete(User user);
}
