package com.noteit.noteit.repositories;

import com.noteit.noteit.entities.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, String> {
    Optional<TestEntity> findByUsername(String username);
}
