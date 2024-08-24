package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.exception_handling.NoSuchUserException;
import ru.kata.spring.boot_security.demo.exception_handling.UserIncorrectData;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.annotation.security.PermitAll;
import java.util.List;

@RestController
@RequestMapping("api")
public class RestControllerMVC {
    final UserService userService;
    final RoleService roleService;

    @Autowired
    public RestControllerMVC(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.findAllOrderedById();
    }

    //PathVariable - переменная из самой URL
    @GetMapping("/users/{username}")
    public User getUserById(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new NoSuchUserException(username);
        }

        return user;
    }
    //RequestBody связывает тело HTTP post метода с параметром метода контроллера
    //в методе post обычно отправляются объекты, именно об этом теле речь
    //конвертация из JSON в объект осуществляется благодаря Jackson
    @PostMapping("/users")
    public User addUser(@RequestBody User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));

        userService.save(user);
        return user;
    }

    @ExceptionHandler
    public ResponseEntity<UserIncorrectData> handleException
            (NoSuchUserException exception) {
        UserIncorrectData data = new UserIncorrectData();
        data.setMessage(exception.getMessage());

        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(userService.findById(user.getId()).getPassword());
        } else {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(user.getPassword()));
        }

        userService.save(user);

        return user;
    }

    @DeleteMapping("/users/{username}")
    public String deleteUser(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new NoSuchUserException(username);
        } else {
            userService.delete(user);
        }
        return "User with name " + username + " was deleted";
    }

    @PostMapping("/save-user-template")
    @PermitAll
    public ResponseEntity<String> saveUser(@ModelAttribute("user") User user) {
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
