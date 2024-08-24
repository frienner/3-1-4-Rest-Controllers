package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;


import java.util.List;

@Controller
@RequestMapping
public class AdminController {
    final UserService userService;
    final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        List<User> users = userService.findAllOrderedById();
        model.addAttribute("users", users);
        model.addAttribute("availableRoles", roleService.findAll());

        // Получаем текущего авторизованного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // Имя пользователя
            User currentUser = userService.findByUsername(username); // Получаем объект пользователя по имени
            model.addAttribute("currentUser", currentUser);
        }
        return "admin";
    }

    @GetMapping("/add-user")
    public String addUserPage(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("availableRoles", roleService.findAll());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // Имя пользователя
            User currentUser = userService.findByUsername(username); // Получаем объект пользователя по имени
            model.addAttribute("currentUser", currentUser);
        }

        return "add-user";
    }

}
