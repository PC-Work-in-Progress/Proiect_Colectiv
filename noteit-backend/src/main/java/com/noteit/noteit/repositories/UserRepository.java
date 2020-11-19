package com.noteit.noteit.repositories;

import com.noteit.noteit.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByToken(String token);
}
