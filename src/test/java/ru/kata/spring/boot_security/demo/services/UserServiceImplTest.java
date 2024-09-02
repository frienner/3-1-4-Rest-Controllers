package ru.kata.spring.boot_security.demo.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    //какого то хера при попытке открыть моки вручную, при помощи openMocks, ломается код
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

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
    void UserServiceImpl_Save_ReturnSavedUser() {
        when(userRepository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.save(user1);

        assertThat(savedUser).isEqualTo(user1);
    }

    @Test
    void UserServiceImpl_FindAll_returnAllUsers() {
        List<User> actualUsers = Arrays.asList(user1, user2);

        when(userRepository.findAll())
                .thenReturn(actualUsers);

        List<User> usersFromRepo = userService.findAll();

        assertThat(usersFromRepo).isEqualTo(actualUsers);
    }

    @Test
    void UserServiceImpl_FindById_InvalidId_ShouldReturnNull() {
        when(userRepository.findById(Mockito.anyLong())
                ).thenReturn(Optional.empty());

        User actualUser = userService.findById(1L);

        assertThat(actualUser).isNull();
    }

    @Test
    void UserServiceImpl_FindById_ValidId_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        User userFromRepo = userService.findById(1L);

        Assertions.assertEquals(user1, userFromRepo);
    }

    @Test
    void UserServiceImpl_Delete_CorrectEntity_ShouldDeleteUser() {
        userService.delete(user1);

        verify(userRepository, times(1)).delete(user1);
    }
}