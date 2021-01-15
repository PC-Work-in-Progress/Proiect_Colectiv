package com.noteit.noteit.services;

import com.noteit.noteit.entities.TagEntity;

import java.util.List;

public interface TagServiceInterface {
    List<TagEntity> getAllPredefinedTags(Integer predefined);
}
