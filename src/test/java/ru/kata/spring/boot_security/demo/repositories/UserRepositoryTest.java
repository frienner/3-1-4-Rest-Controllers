package ru.kata.spring.boot_security.demo.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DataJpaTest //тестируем методы, связанные с базой данных (JPA)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2) //локальная in-memory база данных
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    private User user1, user2;

    @BeforeEach
    void clearDatabase() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

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
    void UserRepository_save_savesUser() {
        User savedUser = userRepository.save(user1);

        Assertions.assertThat(savedUser).isNotNull();
        //or
        Assertions.assertThat(savedUser.getId()).isGreaterThan(0);
    }

    @Test
    public void UserRepository_findAll_returnMoreThanOneUser() {
        userRepository.save(user1);
        userRepository.save(user2);

        List<User> users = userRepository.findAll();

        Assertions.assertThat(users.size()).isEqualTo(2);
    }

    @Test
    void UserRepository_findById_returnDesiredUser() {
        User savedUser = userRepository.save(user1);

        User foundUser = userRepository.findById(savedUser.getId()).orElseThrow();

        Assertions.assertThat(savedUser).isEqualTo(foundUser);
    }

    @Test
    void UserRepository_findAllOrderedById_returnAllUsersOrderedById() {
        User user3 = User.builder()
                .username("test3@gmail.com").password("1")
                .firstName("Ilya").lastName("Zubkov")
                .age(22)
                .roles(List.of(Role.builder().id(1L).name("ROLE_ADMIN").build()))
                .build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        List<User> expectedUsers = Stream.of(user1, user2, user3)
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
        List<User> usersFromRepository = userRepository.findAllOrderedById();

        Assertions.assertThat(usersFromRepository).isEqualTo(expectedUsers);

    }
}