package com.noteit.noteit.tags.repository;

import com.noteit.noteit.tags.model.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, String> {
    Optional<TagEntity> findById(String s);
    TagEntity findByName(String name);
    List<TagEntity> findAllByPredefined(Integer predef);
}
