package com.noteit.noteit.files.controller;

import com.noteit.noteit.files.message.ResponseMessage;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomCompositePK;
import com.noteit.noteit.files.model.FileRoomDB;
import com.noteit.noteit.files.service.FileStorageService;
import com.noteit.noteit.files.service.FileStorageServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@CrossOrigin
public class FileController {
    @Autowired
    private FileStorageService service;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file")MultipartFile file){
                String message = "";
        System.out.println("a intrat");
        System.out.println(file.getName());
        System.out.println(file.getContentType());
        FileDB f = null;
        try {
            f = new FileDB("dsadsah", file.getContentType(), file.getBytes(), 0, "data", "fdsa", file.getBytes().length);
        }catch (Exception e){
            System.out.println("eroare");
        }
        var a = service.add(f);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("a mers"));

    }

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {
        FileDB fileDB = service.getById(id);
        //FileRoomDB a = service.addFileRoom(new FileRoomDB(new FileRoomCompositePK("id2", "6c8cc747-3764-4123-954e-e9c0365e2290")));
        var a = service.findByRoomId("id2");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(fileDB.getUploaded_file());
    }

}
