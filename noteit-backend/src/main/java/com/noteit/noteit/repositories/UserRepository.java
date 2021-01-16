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

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.username = :usernameParam WHERE user.token = :jwtParam")
    void updateUsername(@Param("jwtParam") String jwt, @Param("usernameParam") String username);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.full_name = :fullNameParam WHERE user.token = :jwtParam")
    void updateFullName(@Param("jwtParam") String jwt, @Param("fullNameParam") String fullName);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.password = :passwordParam WHERE user.token = :jwtParam")
    void updatePassword(@Param("jwtParam") String jwt, @Param("passwordParam") String password);

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity user SET user.email = :emailParam WHERE user.token = :jwtParam")
    void updateEmail(@Param("jwtParam") String jwt, @Param("emailParam") String username);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserEntity user WHERE user.username = :username")
    void deleteUser(@Param("username") String username);
//
//    UserEntity findById(Integer id);
}
