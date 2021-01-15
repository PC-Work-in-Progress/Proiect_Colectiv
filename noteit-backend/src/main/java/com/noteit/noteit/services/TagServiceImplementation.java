package com.noteit.noteit.services;

import com.noteit.noteit.entities.TagEntity;
import com.noteit.noteit.repositories.TagRepository;
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
