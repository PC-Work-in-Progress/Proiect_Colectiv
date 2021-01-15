package com.noteit.noteit.repositories;

import com.noteit.noteit.entities.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.tags.form.TagWriter;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, String> {
    Optional<TagEntity> findById(String s);
    TagEntity findByName(String name);
    List<TagEntity> findAllByPredefined(Integer predef);
}
