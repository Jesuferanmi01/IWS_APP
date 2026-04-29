package com.understandingjava.iws_app.Repos;

import com.understandingjava.iws_app.Models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface IAuthRepo extends JpaRepository<Users, UUID> {
    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) FROM Users u")
    long countAllUsers();

    Users findUserById(UUID id);

    Users findByUserCode(String userCode);

    Users findByEmail(String email);


    Users findUsersById(UUID id);
}