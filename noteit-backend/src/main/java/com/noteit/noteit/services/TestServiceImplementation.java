package com.noteit.noteit.services;

import com.noteit.noteit.entities.TestEntity;
import com.noteit.noteit.repositories.TestRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class TestServiceImplementation implements TestServiceInterface{
    private TestRepository repository;

    @Override
    public TestEntity getByUsername(String username){
        return repository.findByUsername(username).get();
    }
}
