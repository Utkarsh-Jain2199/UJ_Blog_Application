package com.mountblue.blogapplication.repository;

import com.mountblue.blogapplication.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Add custom query methods if needed
    User findByEmail(String email);

}