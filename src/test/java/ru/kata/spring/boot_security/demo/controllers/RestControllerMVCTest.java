package ru.kata.spring.boot_security.demo.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.kata.spring.boot_security.demo.configs.SuccessUserHandler;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;
import ru.kata.spring.boot_security.demo.services.UserServiceImpl;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = RestControllerMVC.class)
@AutoConfigureMockMvc(addFilters = false)//отключение spring security, чтобы не нужно было добавлять токены -> права доступа
class RestControllerMVCTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;

    @MockBean
    private SuccessUserHandler successUserHandler;

    @Autowired
    private ObjectMapper objectMapper; //позволяет упростить маппинг сущностей

    private User user1, user2;

    @BeforeEach
    void setUp() {

        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("ROLE_USER");

        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("ROLE_ADMIN");

        user1 = User.builder()
                .username("test1@gmail.com").password("1")
                .firstName("Ilya").lastName("Zubkov")
                .age(22).roles(Arrays.asList(role1, role2))
                .build();
        user2 = User.builder()
                .username("test2@gmail.com").password("1")
                .firstName("Papich").lastName("Velichaishii")
                .age(30).roles(List.of(role1))
                .build();
    }

    @Test
    public void RESTController_AddUser_CorrectData_ReturnCreated() throws Exception {
        given(userService.save(ArgumentMatchers.any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(user1.getUsername()))
                .andExpect(jsonPath("$.firstName").value(user1.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user1.getLastName()))
                .andExpect(jsonPath("$.age").value(user1.getAge()));
    }

    @Test
    public void RESTController_getAllUsers_ReturnAllOrderedById() throws Exception {
        User user3 = User.builder()
                .username("test3@gmail.com").password("1")
                .firstName("Ilya").lastName("Zubkov")
                .age(22)
                .roles(List.of(Role.builder().id(1L).name("ROLE_ADMIN").build()))
                .build();

        List<User> users = Arrays.asList(user1, user2, user3);

        when(userService.findAllOrderedById()).thenReturn(users);

        ResultActions response = mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(users.size()))
                .andExpect(jsonPath("$[0].username").value(user1.getUsername()))
                .andExpect(jsonPath("$[1].username").value(user2.getUsername()))
                .andExpect(jsonPath("$[2].username").value(user3.getUsername()));

    }

    @Test
    public void RESTController_getUserById_ReturnUser() throws Exception {
        int id = 1;
        when(userService.findById(id)).thenReturn(user1);

        ResultActions response = mockMvc.perform(get("/api/users/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));
    }

    @Test
    public void RESTController_updateUser_ReturnUser() throws Exception {
        when(userService.save(user1)).thenReturn(user1);

        ResultActions response = mockMvc.perform(put("/api/users/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user1.getUsername()));

    }

    @Test
    public void RESTController_DeleteUser_UserExists_ReturnString() throws Exception {
        String username = "test1@gmail.com";

        when(userService.findByUsername(username)).thenReturn(user1);
        doNothing().when(userService).delete(user1);

        ResultActions response = mockMvc.perform(delete("/api/users/" + username)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                ;
    }

    @Test
    public void RESTController_DeleteUser_UserDoesntExists_UserNotFoundStatus() throws Exception {
        String username = "incorrectUsername@gmail.com";

        when(userService.findByUsername(username))
                .thenThrow(new UsernameNotFoundException("User with username" + username + " not found"));

        ResultActions response = mockMvc.perform(delete("/api/users/" + username))
                .andExpect(status().isNotFound());
    }


}