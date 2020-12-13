package com.noteit.noteit.files.controller;

import com.noteit.noteit.entities.UserEntity;
import com.noteit.noteit.files.exception.FileException;
import com.noteit.noteit.files.message.ResponseContentFile;
import com.noteit.noteit.files.message.ResponseFile;
import com.noteit.noteit.files.message.ResponseMessage;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomCompositePK;
import com.noteit.noteit.files.model.FileRoomDB;
import com.noteit.noteit.files.service.FileStorageService;
import com.noteit.noteit.services.RoomServiceImplementation;
import org.hibernate.service.spi.ServiceException;
import com.noteit.noteit.helper.mapper.UserMapper;
import com.noteit.noteit.services.UserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@CrossOrigin
@RequestMapping("/api/files")
public class FileController {
    @Autowired
    private FileStorageService fileService;

    @Autowired
    private UserServiceImplementation userService;

    @Autowired
    private RoomServiceImplementation roomService;

    @PostMapping("/UploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam  String roomId, @RequestParam("file") MultipartFile file, @RequestParam("tags") String tags,  @RequestHeader Map<String, String> headers) {
        String message = "";
        String fullToken = headers.get("authorization");
        if (fullToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action!"));
        }
        var elems =fullToken.split(" ");
        String token = elems[1];

        try {
            roomService.getById(roomId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid room id"));
        }

        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        }catch (Exception e) {

        }
        if (userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! Invalid token"));
        }

        try {
            FileDB f = fileService.store(file, userId, roomId, tags);
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/files/")
                    .path(f.getId())
                    .toUriString();
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseFile(
                    f.getName(),
                    fileDownloadUri,
                    f.getType(),
                    f.getSize(),
                    f.getDate(),
                    userService.getUsernameById(userId),
                    f.getId()));
        } catch (FileException e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "! " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/ApprovedFiles")
    public ResponseEntity<?> getApprovedFilesForRoom(@RequestParam  String roomId, @RequestHeader Map<String, String> headers) {
        String fullToken = headers.get("authorization");
        if (fullToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action!"));
        }
        var elems =fullToken.split(" ");
        String token = elems[1];

        if (roomId == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Room Id not specified!"));
        }

        try {
            roomService.getById(roomId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid room id"));
        }


        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        }catch (Exception e) {

        }
        if (userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! Invalid token"));
        }

        List<ResponseFile> files = fileService.getFilesForRoom(roomId).filter(x->x.getApproved()==1).map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/files/")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getSize(),
                    dbFile.getDate(),
                    userService.getUsernameById(dbFile.getUser_id()),
                    dbFile.getId());
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/InReviewFiles")
    public ResponseEntity<?> getInReviewFiles(@RequestParam  String roomId, @RequestHeader Map<String, String> headers) {
        String fullToken = headers.get("authorization");
        if (fullToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action!"));
        }
        var elems =fullToken.split(" ");
        String token = elems[1];

        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        }catch (Exception e) {

        }
        if (userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! Invalid token"));
        }

        if (roomId == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Room Id not specified!"));
        }

        try {
            roomService.getById(roomId);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid room id"));
        }

        List<ResponseFile> files = fileService.getFilesForRoom(roomId).filter(x->x.getApproved()==0).map(dbFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/files/")
                    .path(dbFile.getId())
                    .toUriString();

            return new ResponseFile(
                    dbFile.getName(),
                    fileDownloadUri,
                    dbFile.getType(),
                    dbFile.getSize(),
                    dbFile.getDate(),
                    userService.getUsernameById(dbFile.getUser_id()),
                    dbFile.getId());
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFile(@PathVariable String id, @RequestHeader Map<String, String> headers) {
        String fullToken = headers.get("authorization");
        if (fullToken == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action!"));
        }
        var elems =fullToken.split(" ");
        String token = elems[1];

        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        }catch (Exception e) {

        }
        if (userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! Invalid token"));
        }

        FileDB fileDB;
        try {
            fileDB = fileService.getById(id);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("File not found"));

        }


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(new ResponseContentFile(fileDB.getUploaded_file() ,fileDB.getName()));
    }


    @GetMapping("/details/{id}")
    public ResponseEntity<?> getDetailsFile(@PathVariable String id) {
        try {
            return ResponseEntity.ok().body(fileService.getDetails(id));
        } catch (ServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable String id) {
        Optional<FileDB> optionalFile = fileService.getFile(id);

        if(optionalFile.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage("No such file!"));

        FileDB file = optionalFile.get();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment:filename=\"" + file.getName() + "\"")
                .body(new ByteArrayResource(file.getUploaded_file()));
    }

    @PutMapping("/AcceptFile/{id}")
    public ResponseEntity<ResponseMessage> acceptFile(@PathVariable String id, @RequestHeader Map<String, String> headers) {
        String fullToken = headers.get("authorization");
        if (fullToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action!"));
        }
        var elems = fullToken.split(" ");
        String token = elems[1];

        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        } catch (Exception e) {

        }
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! Invalid token"));
        }

        try {
            var f = fileService.acceptFile(id);
            if (f != null)
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("File accepted successfully!"));
            else
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage("Conflict with current state! File already accepted!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Could not accept this file! Invalid id!"));
        }
    }

    @PutMapping("/DenyFile/{id}")
    public ResponseEntity<ResponseMessage> denyFile(@PathVariable String id, @RequestHeader Map<String, String> headers) {
        String fullToken = headers.get("authorization");
        if (fullToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action!"));
        }
        var elems = fullToken.split(" ");
        String token = elems[1];

        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        } catch (Exception e) {

        }
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! Invalid token"));
        }


        try {
            var f = fileService.denyFile(id);
            if (f == null)
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("File denied successfully!"));
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Unexpected file id provided!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Could not deny this file! Invalid id!"));
        }
    }


}
