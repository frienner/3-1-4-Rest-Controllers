package ru.kata.spring.boot_security.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username); //эта штука магическим образом ищет по имени пользователя без реализации
    //под капотом вызывается метод invoke из JpaRepository
    //посмотреть, как это реализовано, какие похожие кейсы использования есть
    @Query("SELECT u FROM User u ORDER BY u.id ASC")
    List<User> findAllOrderedById();

    User findById(long id);
}