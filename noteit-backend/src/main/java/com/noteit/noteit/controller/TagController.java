package com.noteit.noteit.controller;

import com.noteit.noteit.services.TagServiceImplementation;
import lombok.AllArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
@CrossOrigin
@AllArgsConstructor
public class TagController {
    TagServiceImplementation tagService;


    /**
     *
     * @return A list containing all predefined tags
     */
    @GetMapping("/predefined")
    public ResponseEntity<?> getPredefinedTags(){
        try
        {
            return ResponseEntity.ok().body(tagService.getAllPredefinedTags(1));
        }
        catch (ServiceException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
