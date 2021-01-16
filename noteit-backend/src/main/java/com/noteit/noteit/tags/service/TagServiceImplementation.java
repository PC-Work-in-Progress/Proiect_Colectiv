package com.noteit.noteit.tags.service;

import com.noteit.noteit.tags.model.TagEntity;
import com.noteit.noteit.tags.repository.TagRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class TagServiceImplementation {
    private TagRepository tagRepository;

    public List<TagEntity> getAllPredefinedTags(Integer predefined){
        return tagRepository.findAllByPredefined(predefined);
    }
}
