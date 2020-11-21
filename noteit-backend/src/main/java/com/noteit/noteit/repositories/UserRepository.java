package com.noteit.noteit.repositories;

import com.noteit.noteit.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByToken(String token);
    UserEntity findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT max(user.id) FROM UserEntity user")
    Long getMaxId();

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.token = :jwtParam WHERE user.username = :usernameParam")
    void updateToken(@Param("usernameParam") String username, @Param("jwtParam") String jwt);

//
//    UserEntity findById(Integer id);
}
