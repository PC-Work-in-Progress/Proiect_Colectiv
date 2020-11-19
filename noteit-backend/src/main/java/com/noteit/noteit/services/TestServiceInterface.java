package com.noteit.noteit.services;

import com.noteit.noteit.entities.TestEntity;

public interface TestServiceInterface {
    TestEntity getByUsername(String username);
}
