package com.noteit.noteit.tags.service;

import com.noteit.noteit.tags.model.TagEntity;

import java.util.List;

public interface TagServiceInterface {
    List<TagEntity> getAllPredefinedTags(Integer predefined);
}
