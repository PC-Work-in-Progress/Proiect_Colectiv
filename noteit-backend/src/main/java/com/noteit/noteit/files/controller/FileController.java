package com.noteit.noteit.files.controller;

import com.noteit.noteit.entities.UserEntity;
import com.noteit.noteit.files.exception.FileException;
import com.noteit.noteit.files.message.ResponseFile;
import com.noteit.noteit.files.message.ResponseMessage;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomCompositePK;
import com.noteit.noteit.files.model.FileRoomDB;
import com.noteit.noteit.files.service.FileStorageService;
import com.noteit.noteit.files.service.FileStorageServiceInterface;
import com.noteit.noteit.helper.mapper.UserMapper;
import com.noteit.noteit.services.UserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
public class FileController {
    @Autowired
    private FileStorageService fileService;

    @Autowired
    private UserServiceImplementation userService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader Map<String, String> headers) {
        String message = "";

        String token = headers.get("authorization");
        String roomId = headers.get("roomid");
        if (token == null){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Anauthorized action!"));
        }
        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Room Id not specified!"));
        }

        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        }catch (Exception e) {

        }
        if (userId == null){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Anauthorized action! Invalid token"));
        }

        try {
            fileService.store(file, userId, roomId);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
        } catch (FileException e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "! " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<?> getFilesForRoom(@RequestHeader Map<String, String> headers) {

        String roomId = headers.get("roomid");
        String token = headers.get("authorization");

        if (token == null){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Anauthorized action!"));
        }
        if (roomId == null){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Room Id not specified!"));
        }

        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        }catch (Exception e) {

        }
        if (userId == null){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Anauthorized action! Invalid token"));
        }

        List<ResponseFile> files = fileService.getFilesForRoom(roomId).map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/files/")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getSize(),
                    dbFile.getDate());
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/files/{id}")
    public ResponseEntity<?> getFile(@PathVariable String id, @RequestHeader Map<String, String> headers) {
        String token = headers.get("authorization");
        if (token == null){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Anauthorized action!"));
        }

        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        }catch (Exception e) {

        }
        if (userId == null){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("Anauthorized action! Invalid token"));
        }

        FileDB fileDB = fileService.getById(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(fileDB.getUploaded_file());
    }

}
