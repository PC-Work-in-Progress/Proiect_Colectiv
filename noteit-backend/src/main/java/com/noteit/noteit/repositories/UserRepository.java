package com.noteit.noteit.repositories;

import com.noteit.noteit.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByToken(String token);
    UserEntity findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT max(user.id) FROM UserEntity user")
    Long getMaxId();
//
//    UserEntity findById(Integer id);
}
