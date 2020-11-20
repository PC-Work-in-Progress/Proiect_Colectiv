package com.noteit.noteit.files.repository;

import com.noteit.noteit.files.model.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {
    FileDB findByNameAndTypeAndSize(String name, String type, Integer size);
}
