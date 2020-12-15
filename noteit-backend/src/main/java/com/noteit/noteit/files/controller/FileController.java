package com.noteit.noteit.files.controller;

import com.noteit.noteit.files.exception.FileException;
import com.noteit.noteit.files.message.ResponseFile;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
public class FileController {
    @Autowired
    private FileStorageService service;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader Map<String, String> headers) {
        String message = "";
        System.out.println("a intrat");
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getContentType());
        headers.forEach((key, value) -> {
            System.out.println((String.format("Header '%s' = %s", key, value)));
        });
        String userId = headers.get("authorization");
        String roomId = headers.get("roomid");
        // TO DO: valideaza token
        FileDB f = null;
        try {
            service.store(file, userId, roomId);
            //f = new FileDB("dsadsah", file.getContentType(), file.getBytes(), 0, "data", "fdsa", file.getBytes().length);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (FileException e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "! " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        } catch (Exception e) {
            //e.printStackTrace();
            System.out.println("eroare");
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<ResponseFile>> getFilesForRoom(@RequestHeader Map<String, String> headers) {
        // TO DO: valideaza token
        String roomId = headers.get("roomid");
//        if (roomId == null){
//            return ResponseEntity.status(HttpStatus.
//        }
        List<ResponseFile> files = service.getFilesForRoom(roomId).map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getSize());
        }).collect(Collectors.toList());
        var b = files;

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable String id) {

        // TO DO: valideaza token
        FileDB fileDB = service.getById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(fileDB.getUploaded_file());
    }

    @PostMapping("/recognition")
    public ResponseEntity<byte[]> getTextRecognition(@RequestParam("file") MultipartFile file, @RequestHeader Map<String, String> headers){
        String userId = headers.get("authorization");
        try {
            String resultPath = service.detectHandwriting(file, userId);
            File f = new File(resultPath);

            // work only for 2GB file, because array index can only up to Integer.MAX
            // change it into something that can support bigger files

            byte[] buffer = new byte[(int)f.length()];
            FileInputStream is = new FileInputStream(resultPath);
            is.read(buffer);
            is.close();

            return ResponseEntity.status(HttpStatus.OK).body(buffer);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
        }
    }

}
