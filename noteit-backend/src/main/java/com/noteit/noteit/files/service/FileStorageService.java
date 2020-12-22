package com.noteit.noteit.files.service;

import com.noteit.noteit.entities.FileTagEntity;
import com.noteit.noteit.entities.FileTagPK;
import com.noteit.noteit.entities.RoomEntity;
import com.noteit.noteit.entities.UserEntity;
import com.noteit.noteit.entities.TagEntity;
import com.noteit.noteit.entities.UserRoomEntity;
import com.noteit.noteit.files.dtos.FileDbDto;
import com.noteit.noteit.files.dtos.FileDbWrapper;
import com.noteit.noteit.files.dtos.FileRoomDto;
import com.noteit.noteit.files.exception.FileException;
import com.noteit.noteit.files.mapper.FileDbMapper;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomCompositePK;
import com.noteit.noteit.files.model.FileRoomDB;
import com.noteit.noteit.files.repository.FileDBRepository;
import com.noteit.noteit.files.repository.FileRoomDBRepository;
import com.noteit.noteit.hwrecognition.TextDetector;
import com.noteit.noteit.repositories.*;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileStorageService implements FileStorageServiceInterface {
    @Autowired
    private FileDBRepository fileDBRepository;

    @Autowired
    private FileRoomDBRepository fileRoomDBRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private FileTagRepository fileTagRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    private FileDbMapper fileDbMapper = new FileDbMapper();


    @Override
    public FileDB getById(String id) {
        return fileDBRepository.findById(id).get();
    }

    /**
     * @param file   file to be saved
     * @param userId id of user that sent the file
     * @param roomId id of room where we save the file
     * @param tags   string containing the tags given for file
     * @return saved FileDB
     * @throws IOException
     * @throws FileException if file is already in current room
     */
    @Override
    @Transactional
    public FileDB store(MultipartFile file, String userId, String roomId, String tags) throws IOException, FileException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Long size = (long)file.getBytes().length;
        FileDB cautat = fileDBRepository.findByNameAndTypeAndSize(fileName, file.getContentType(), size);

        if (cautat != null) {
            //fisierul ce se vrea a fi adaugat exista deja

            //verificam daca se gaseste in room ul in care vrem sa adauugam
            List<FileRoomDB> cautat2 = fileRoomDBRepository.findById_FileIdAndId_RoomId(cautat.getId(), roomId);
            int nrAparitii = cautat2.size();
            if (nrAparitii != 0) {
                System.out.println("se adauga in acelasi room");
                //exista deja fisierul in room ul in care vrea sa se adauge
                throw new FileException("File already in this room");

            } else {
                System.out.println("se adauga in alt room");
                //exista fisierul in alt room
                fileRoomDBRepository.save(new FileRoomDB(new FileRoomCompositePK(roomId, cautat.getId(), userId)));
                saveTags(tags, cautat.getId());
                return cautat;
            }
        } else {
            //nu exista fisierul in BD
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            String data = formatter.format(date);
            FileDB fileDB = new FileDB(fileName, file.getContentType(), file.getBytes(), data);
            FileDB saved = fileDBRepository.save(fileDB);
            FileRoomDB f = new FileRoomDB(new FileRoomCompositePK(roomId, saved.getId(), userId));
            fileRoomDBRepository.save(f);
            saveTags(tags, saved.getId());
            return saved;
        }

    }


    /**
     * @param tags   string that contains tags separated with ,
     * @param fileId id of file that contains given tags
     */
    private void saveTags(String tags, String fileId) {
        String[] elems = tags.strip().split(",");
        for (String tag : elems) {
            if (tagRepository.findByName(tag) == null) {
                TagEntity t = new TagEntity();
                t.setName(tag);
                TagEntity savedTag = tagRepository.save(t);
                FileTagEntity fileTagEntity = new FileTagEntity();
                fileTagEntity.setId(new FileTagPK(fileId, savedTag.getId()));
                fileTagRepository.save(fileTagEntity);
            }
        }
    }

    /**
     * function that returns a stream with all files in a given room
     *
     * @param roomId id of room
     * @return stream with files for specified room
     */
    @Override
    public Stream<FileDB> getFilesForRoom(String roomId) {
        List<FileRoomDB> fileRoomDBList = fileRoomDBRepository.findById_RoomId(roomId);
        var a = fileRoomDBList;
        List<FileDB> rez = fileRoomDBList.stream().map(x -> fileDBRepository.findById(x.getId().getFileId()).get()).collect(Collectors.toList());
        return rez.stream();
    }

    /*
        Get the relevant details of a file for an user and return the specific DTO
        params: idFile - String
        return dto - FileDbDto
        throw exception when no file was found with the given id
     */
    @Override
    public FileDbDto getDetails(String id) {
//        Optional<FileDB> fileDBOptional = fileDBRepository.findById(id);
//        if(fileDBOptional.isEmpty())
//            throw new ServiceException("No file for this id");
//
//        FileDB fileDB = fileDBOptional.get();
//        List<FileTagEntity> entities = fileTagRepository.findById_FileId(fileDB.getId()).get();
//        List<String> tags = new ArrayList<>();
//        for(FileTagEntity ft: entities) {
//            tags.add(tagRepository.findById(ft.getId().getTagId()).get().getName());
//        }
//        return fileDbMapper.toDto(fileDB, userRepository.findById(fileDB.getUser_id()).get().getUsername(), tags);
        return null;
    }


    /*
        Get whole file with its content
        params: id - String
        return Optional<FileDB>
     */
    @Override
    public Optional<FileDB> getFile(String id) {
        return fileDBRepository.findById(id);
    }


    /**
     * function that marks a file as accepted
     *
     * @param fileId id of file to accepted
     * @return the modified FileDB entitu
     */
    @Override
    public void acceptFile(String fileId, String roomId) throws FileException {
        var f = fileDBRepository.findById(fileId);
        if (f.isEmpty())
            throw new FileException("File not found!");

        var fileRoom = fileRoomDBRepository.findById_FileIdAndId_RoomId(fileId, roomId).get(0);
        if (fileRoom.isAccepted())
            throw new FileException("Conflict with current state! File already accepted!");
        fileRoomDBRepository.delete(fileRoom);
        fileRoom.Accept();
        fileRoomDBRepository.save(fileRoom);
    }

    /**
     * function that marks a file as denied
     *
     * @param fileId id of file to be denied
     * @return the modified FileDB entity
     */
    @Override
    public FileDB denyFile(String fileId, String roomId) {
        var f = fileDBRepository.findById(fileId).get();

        var fileRoomIdDB = fileRoomDBRepository.findById_FileIdAndId_RoomId(fileId, roomId).get(0);
        fileRoomDBRepository.delete(fileRoomIdDB);
        var fileRoomDBList = fileRoomDBRepository.findById_FileId(fileId);
        if (fileRoomDBList.size() == 0)
            fileDBRepository.delete(f);
        return null;
    }

    @Override
    public String detectHandwriting(MultipartFile file, String userId) throws IOException {
        String TEMP_PATH = "src/main/java/com/noteit/noteit/hwrecognition/temp";
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String path = TEMP_PATH + "/images/" + fileName;
        File localFile = new File(path);
        if (localFile.createNewFile()) {
//            file.transferTo(localFile);
            OutputStream os = Files.newOutputStream(Paths.get(path));
            os.write(file.getBytes());
            os.close();
            return TextDetector.detectDocumentText(path);
        } else throw new IOException("Could not process image file " + fileName);
    }

    @Override
    public Stream<FileDbWrapper> getWrappedFilesForRoom(String roomId) {
        return fileRoomDBRepository.findById_RoomId(roomId).stream()
                .map(x -> {
                    var fileDb = fileDBRepository.findById(x.getId().getFileId()).get();
                    return new FileDbWrapper(fileDb.getName(), fileDb.getId(), fileDb.getType(), fileDb.getSize(), fileDb.getDate(), userRepository.findById(x.getId().getUserId()).get().getUsername(), x.getId().getApproved());
                });
    }


    public List<FileRoomDto> getRecentFilesFromToken(String token, int pageNumber) {
        List<FileRoomDto> currentFileRoomDtoList = new ArrayList<>();
        int filesPerPage = 15;
        int startFileIndex = pageNumber * filesPerPage;
        UserEntity user = userRepository.findByToken(token);
        List<UserRoomEntity> userRoomEntities = userRoomRepository.findUserRoomEntityByUserRoomId_UserId(user.getId());

        List<FileRoomDto> fileRoomDtoList = new ArrayList<>();
        for (UserRoomEntity userRoomEntity : userRoomEntities) {
            String roomId = userRoomEntity.getUserRoomId().getRoomId();
            Optional<RoomEntity> room = roomRepository.findById(roomId);
            if (room.isPresent()) {
                List<FileRoomDB> fileRoomDBList = fileRoomDBRepository.findById_RoomId(room.get().getId());
                for (FileRoomDB fileRoomDB : fileRoomDBList) {
                    Optional<FileDB> fileDB = fileDBRepository.findById(fileRoomDB.getId().getFileId());
                    if (fileDB.isPresent()) {
                        Optional<List<FileTagEntity>> fileTagEntities = fileTagRepository.findById_FileId(fileDB.get().getId());
                        if (fileTagEntities.isPresent()) {
                            List<String> tagNames = new ArrayList<>();
                            for (FileTagEntity fileTagEntity : fileTagEntities.get()) {
                                Optional<TagEntity> tagEntity = tagRepository.findById(fileTagEntity.getId().getTagId());
                                if (tagEntity.isPresent()) {
                                    tagNames.add(tagEntity.get().getName());
                                }
                            }
                            UserEntity userOwner = userRepository.findById(room.get().getOwnerId()).get();
                            fileRoomDtoList.add(new FileRoomDto(userOwner.getFull_name(), room.get().getName(), room.get().getId(), fileDB.get().getId(), fileDB.get().getName(), fileDB.get().getDate(), tagNames));
                        }
                    }
                }
            }
        }

        Collections.sort(fileRoomDtoList);

        for (int currentFileIndex = startFileIndex; currentFileIndex <= startFileIndex + filesPerPage - 1 && currentFileIndex < fileRoomDtoList.size(); currentFileIndex++) {
            currentFileRoomDtoList.add(fileRoomDtoList.get(currentFileIndex));
        }

        return currentFileRoomDtoList;
    }

    @Override
    public List<FileRoomDto> getSearchedFilesFromName(String token, String filename) {
        UserEntity user = userRepository.findByToken(token);
        List<UserRoomEntity> userRoomEntities = userRoomRepository.findUserRoomEntityByUserRoomId_UserId(user.getId());

        List<FileRoomDto> fileRoomDtoList = new ArrayList<>();
        for (UserRoomEntity userRoomEntity : userRoomEntities) {
            String roomId = userRoomEntity.getUserRoomId().getRoomId();
            Optional<RoomEntity> room = roomRepository.findById(roomId);
            if (room.isPresent()) {
                List<FileRoomDB> fileRoomDBList = fileRoomDBRepository.findById_RoomId(room.get().getId());
                for (FileRoomDB fileRoomDB : fileRoomDBList) {
                    Optional<FileDB> fileDB = fileDBRepository.findById(fileRoomDB.getId().getFileId());
                    if (fileDB.isPresent() && fileDB.get().getName().contains(filename)) {
                        Optional<List<FileTagEntity>> fileTagEntities = fileTagRepository.findById_FileId(fileDB.get().getId());
                        if (fileTagEntities.isPresent()) {
                            List<String> tagNames = new ArrayList<>();
                            for (FileTagEntity fileTagEntity : fileTagEntities.get()) {
                                Optional<TagEntity> tagEntity = tagRepository.findById(fileTagEntity.getId().getTagId());
                                if (tagEntity.isPresent()) {
                                    tagNames.add(tagEntity.get().getName());
                                }
                            }
                            UserEntity userOwner = userRepository.findById(room.get().getOwnerId()).get();
                            fileRoomDtoList.add(new FileRoomDto(userOwner.getFull_name(), room.get().getName(), room.get().getId(), fileDB.get().getId(), fileDB.get().getName(), fileDB.get().getDate(), tagNames));
                        }
                    }
                }
            }
        }

        return fileRoomDtoList;
    }

    @Override
    public List<FileRoomDto> getSearchedFilesFromTag(String token, String tag) {
        UserEntity user = userRepository.findByToken(token);
        List<UserRoomEntity> userRoomEntities = userRoomRepository.findUserRoomEntityByUserRoomId_UserId(user.getId());

        List<FileRoomDto> fileRoomDtoList = new ArrayList<>();
        for (UserRoomEntity userRoomEntity : userRoomEntities) {
            String roomId = userRoomEntity.getUserRoomId().getRoomId();
            Optional<RoomEntity> room = roomRepository.findById(roomId);
            if (room.isPresent()) {
                List<FileRoomDB> fileRoomDBList = fileRoomDBRepository.findById_RoomId(room.get().getId());
                for (FileRoomDB fileRoomDB : fileRoomDBList) {
                    Optional<FileDB> fileDB = fileDBRepository.findById(fileRoomDB.getId().getFileId());
                    if (fileDB.isPresent()) {
                        Optional<List<FileTagEntity>> fileTagEntities = fileTagRepository.findById_FileId(fileDB.get().getId());
                        if (fileTagEntities.isPresent()) {
                            boolean foundTag = false;
                            List<String> tagNames = new ArrayList<>();
                            for (FileTagEntity fileTagEntity : fileTagEntities.get()) {
                                Optional<TagEntity> tagEntity = tagRepository.findById(fileTagEntity.getId().getTagId());
                                if (tagEntity.isPresent()) {
                                    tagNames.add(tagEntity.get().getName());
                                }
                                if (tagEntity.isPresent() && tagEntity.get().getName().contains(tag)) {
                                    foundTag = true;
                                }
                            }
                            if (foundTag == true) {
                                UserEntity userOwner = userRepository.findById(room.get().getOwnerId()).get();
                                fileRoomDtoList.add(new FileRoomDto(userOwner.getFull_name(), room.get().getName(), room.get().getId(), fileDB.get().getId(), fileDB.get().getName(), fileDB.get().getDate(), tagNames));
                            }
                        }
                    }
                }
            }
        }

        return fileRoomDtoList;
    }
}
