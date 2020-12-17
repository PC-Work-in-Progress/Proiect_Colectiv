package com.noteit.noteit.repositories;

import com.noteit.noteit.entities.FileTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileTagRepository extends JpaRepository<FileTagEntity, String> {
    Optional<List<FileTagEntity>> findById_FileId(String id);
    Optional<List<FileTagEntity>> findById_TagId(String id);
}
