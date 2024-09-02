package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.exception_handling.NoSuchUserException;
import ru.kata.spring.boot_security.demo.exception_handling.UserIncorrectData;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import jakarta.annotation.security.PermitAll;
import java.util.List;

@RestController
@RequestMapping("api")
public class RestControllerMVC {
    final UserService userService;
    final RoleService roleService;

    public RestControllerMVC(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllOrderedById();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new NoSuchUserException(username);
        }
        return ResponseEntity.ok(user);
    }

    //RequestBody связывает тело HTTP post метода с параметром метода контроллера
    //в методе post обычно отправляются объекты, именно об этом теле речь
    //конвертация из JSON в объект осуществляется благодаря Jackson
    @PostMapping("/users")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));

        userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @ExceptionHandler
    public ResponseEntity<UserIncorrectData> handleException
            (UsernameNotFoundException exception) {
        UserIncorrectData data = new UserIncorrectData();
        data.setMessage(exception.getMessage());

        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(userService.findById(user.getId()).getPassword());
        } else {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
        }

        userService.save(user);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        User user = userService.findByUsername(username);
        //если не найдет - будет выброшено исключение UsernameNotFound
        userService.delete(user);
        return ResponseEntity.ok("User with name " + username + " was deleted");
    }


    @PostMapping("/save-user-template")
    @PermitAll
    public ResponseEntity<String> saveUser(@ModelAttribute User user) {
        try {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));

            userService.save(user);

            return ResponseEntity.ok("User saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to save user");
        }
    }
}
