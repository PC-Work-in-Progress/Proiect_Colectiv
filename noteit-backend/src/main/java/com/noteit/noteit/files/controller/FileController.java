package com.noteit.noteit.files.controller;

import com.noteit.noteit.entities.RoomEntity;
import com.noteit.noteit.files.exception.FileException;
import com.noteit.noteit.files.message.ResponseContentFile;
import com.noteit.noteit.files.message.ResponseFile;
import com.noteit.noteit.files.message.ResponseMessage;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.service.FileStorageService;
import com.noteit.noteit.notifications.service.NotificationsService;
import com.noteit.noteit.services.RoomServiceImplementation;
import org.hibernate.service.spi.ServiceException;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    @Autowired
    private NotificationsService notificationsService;

    @PostMapping("/UploadFile")
    public ResponseEntity<?> uploadFile(@RequestParam String roomId, @RequestParam("file") MultipartFile file, @RequestParam("tags") String tags, @RequestHeader Map<String, String> headers) {
        String message = "";
        String fullToken = headers.get("authorization");
        if (fullToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action!"));
        }
        var elems = fullToken.split(" ");
        String token = elems[1];

        RoomEntity r = roomService.getById(roomId);
        if (r == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid room id"));

        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        } catch (Exception e) {

        }
        if (userId == null) {
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
    public ResponseEntity<?> getApprovedFilesForRoom(@RequestParam String roomId, @RequestHeader Map<String, String> headers) {
        String fullToken = headers.get("authorization");
        if (fullToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action!"));
        }
        var elems = fullToken.split(" ");
        String token = elems[1];

        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Room Id not specified!"));
        }

        RoomEntity r = roomService.getById(roomId);
        if (r == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid room id"));


        String userId = null;
        try {
            userId = userService.getUserIdByToken(token);
        } catch (Exception e) {

        }
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! Invalid token"));
        }

        List<ResponseFile> files = fileService.getWrappedFilesForRoom(roomId).filter(x -> x.getApproved() == 1).map(wrappedFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/files/")
                    .path(wrappedFile.getFileId())
                    .toUriString();

            return new ResponseFile(
                    wrappedFile.getFileName(),
                    fileDownloadUri,
                    wrappedFile.getFileType(),
                    wrappedFile.getSize(),
                    wrappedFile.getDate(),
                    wrappedFile.getUsername(),
                    wrappedFile.getFileId());
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/InReviewFiles")
    public ResponseEntity<?> getInReviewFiles(@RequestParam String roomId, @RequestHeader Map<String, String> headers) {
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

        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Room Id not specified!"));
        }

        RoomEntity r = roomService.getById(roomId);
        if (r == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid room id"));

        List<ResponseFile> files = fileService.getWrappedFilesForRoom(roomId).filter(x -> x.getApproved() == 0).map(wrappedFile -> {
            String fileDownloadUri = ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/files/")
                    .path(wrappedFile.getFileId())
                    .toUriString();

            return new ResponseFile(
                    wrappedFile.getFileName(),
                    fileDownloadUri,
                    wrappedFile.getFileType(),
                    wrappedFile.getSize(),
                    wrappedFile.getDate(),
                    wrappedFile.getUsername(),
                    wrappedFile.getFileId());
        }).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFile(@PathVariable String id, @RequestParam String roomId, @RequestHeader Map<String, String> headers) {
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

        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Room Id not specified!"));
        }

        RoomEntity r = roomService.getById(roomId);
        if (r == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid room id"));

        FileDB fileDB;
        try {
            fileDB = fileService.getById(id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage("File not found"));
        }


        fileService.fileViewed(fileDB.getId(), roomId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDB.getName() + "\"")
                .body(new ResponseContentFile(fileDB.getUploaded_file(), fileDB.getName()));
    }


    @GetMapping("/details/{roomId}/{fileId}")
    public ResponseEntity<?> getDetailsFile(@PathVariable String roomId, @PathVariable String fileId, @RequestHeader Map<String, String> headers) {
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! No user"));
        }

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! Invalid token"));
        }

        try {
            return ResponseEntity.ok().body(fileService.getDetails(fileId, roomId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/AcceptFile/{id}")
    public ResponseEntity<ResponseMessage> acceptFile(@PathVariable String id, @RequestParam String roomId, @RequestHeader Map<String, String> headers) {
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

        RoomEntity r = roomService.getById(roomId);
        if (r == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid room id"));

        try {
            fileService.acceptFile(id, roomId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("File accepted successfully!"));
        } catch (FileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Could not accept this file! Something went wrong!"));
        }
    }

    @PutMapping("/DenyFile/{id}")
    public ResponseEntity<ResponseMessage> denyFile(@PathVariable String id, @RequestParam String roomId, @RequestHeader Map<String, String> headers) {
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

        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Room Id not specified!"));
        }

        RoomEntity r = roomService.getById(roomId);
        if (r == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid room id"));

        try {
            var file = fileService.getById(id);
            var toNotifyId = fileService.getUserIdByFileAndRoom(id, roomId);
            var f = fileService.denyFile(id, roomId);
            if (f == null) {
                notificationsService.pushNotification(toNotifyId, "File deleted! Your file: " + file.getName() + " didn't meet the room rules of "+ r.getName()+" room, so it couldn't be approved!");
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("File denied successfully!"));
            }
            else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Unexpected file id provided!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Could not deny this file! File not found in this room!"));
        }
    }

    @PostMapping("/recognition")
    public ResponseEntity<?> getTextRecognition(@RequestParam("file") MultipartFile file, @RequestHeader Map<String, String> headers) {
        String userId = headers.get("authorization");

        if(userId == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseMessage("Unauthorized action! Invalid token"));
        }
        try {
            String resultPath = fileService.detectHandwriting(file, userId);
            File f = new File(resultPath);

            byte[] buffer = new byte[(int) f.length()];
            FileInputStream is = new FileInputStream(resultPath);
            is.read(buffer);
            is.close();

            fileService.removeFromTemp(resultPath);
            return ResponseEntity.status(HttpStatus.OK).body(buffer);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(null);
        }
    }

    @GetMapping("/recentFiles/{pageNumber}")
    public ResponseEntity<?> getRecentFilesFromToken(@PathVariable String pageNumber, HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                throw new ServiceException("No JWT token found in request headers");
            }

            String authToken = header.split(" ")[1];
            if (pageNumber == null) {
                throw new ServiceException("Header params are missing");
            }
            return ResponseEntity.ok().body(fileService.getRecentFilesFromToken(authToken, Integer.parseInt(pageNumber)));
        } catch (ServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/filename/{filename}")
    public ResponseEntity<?> getSearchedFilesByName(@PathVariable String filename, HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                throw new ServiceException("No JWT token found in request headers");
            }
            String authToken = header.split(" ")[1];
            System.out.println(authToken);
            return ResponseEntity.ok().body(fileService.getSearchedFilesFromName(authToken, filename));
        } catch (ServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<?> getSearchedFilesByTag(@PathVariable String tag, HttpServletRequest request) {
        try {
            String header = request.getHeader("Authorization");

            if (header == null || !header.startsWith("Bearer ")) {
                throw new ServiceException("No JWT token found in request headers");
            }
            String authToken = header.split(" ")[1];
            return ResponseEntity.ok().body(fileService.getSearchedFilesFromTag(authToken, tag));
        } catch (ServiceException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("DownloadFile/{id}")
    public ResponseEntity<?> downloadedFile(@PathVariable String id, @RequestParam String roomId, @RequestHeader Map<String, String> headers) {
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

        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Room Id not specified!"));
        }

        RoomEntity r = roomService.getById(roomId);
        if (r == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Invalid room id"));

        try {
            fileService.downloadFile(id, roomId);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage("File download successfully!"));
        } catch (FileException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Could not download this file! Something went wrong!"));
        }

    }
}
