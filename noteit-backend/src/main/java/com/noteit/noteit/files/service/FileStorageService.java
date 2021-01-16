package com.noteit.noteit.files.service;

import com.noteit.noteit.files.model.FileTagEntity;
import com.noteit.noteit.files.model.FileTagPK;
import com.noteit.noteit.files.repository.FileTagRepository;
import com.noteit.noteit.rooms.model.RoomEntity;
import com.noteit.noteit.rooms.repository.RoomRepository;
import com.noteit.noteit.rooms.repository.UserRoomRepository;
import com.noteit.noteit.tags.repository.TagRepository;
import com.noteit.noteit.users.model.UserEntity;
import com.noteit.noteit.tags.model.TagEntity;
import com.noteit.noteit.rooms.model.UserRoomEntity;
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
import com.noteit.noteit.users.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger();


    /**
     * @param id id of file to be found
     * @return FileDB entity if found
     */
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
        logger.info("ENTER store file from user : {} in room : {} with tags {}", userId, roomId, tags);
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
                logger.info("EXIT, file id : {} already in room : {}", cautat.getId(), roomId);
                throw new FileException("File already in this room");

            } else {
                System.out.println("se adauga in alt room");
                //exista fisierul in alt room
                fileRoomDBRepository.save(new FileRoomDB(new FileRoomCompositePK(roomId, cautat.getId(), userId)));
                saveTags(tags, cautat.getId());
                logger.info("EXIT, file already on database, id : {}, successfully added on room {}, RETURN : {}", cautat.getId(), roomId, cautat);
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
            logger.info("EXIT with success, file added on database, id : {}, RETURN : {}", saved.getId(), saved);
            return saved;
        }

    }


    /**
     * @param tags   string that contains tags separated with ,
     * @param fileId id of file that contains given tags
     */
    private void saveTags(String tags, String fileId) {
        logger.info("ENTER saving tags: {} for file : {}", tags, fileId);
        String[] elems = tags.strip().split(",");
        for (String tag : elems) {
            if (tagRepository.findByName(tag) == null) {
                TagEntity t = new TagEntity();
                t.setName(tag);
                TagEntity savedTag = tagRepository.save(t);
                FileTagEntity fileTagEntity = new FileTagEntity();
                fileTagEntity.setId(new FileTagPK(fileId, savedTag.getId()));
                fileTagRepository.save(fileTagEntity);
            } else {
                TagEntity t = tagRepository.findByName(tag);
                FileTagEntity fileTagEntity = new FileTagEntity();
                fileTagEntity.setId(new FileTagPK(fileId, t.getId()));
                fileTagRepository.save(fileTagEntity);
            }
        }
        logger.info("EXIT save tags");
    }


    /**
     * Get the relevant details of a file for an user and return the specific DTO
     * @param fileId - String id of file
     * @param roomId - String id of room
     * @return FileDbDto
     * @throw ServiceException is there is no file with that ID
     */
    @Override
    public FileDbDto getDetails(String fileId, String roomId) throws Exception {
        Optional<FileDB> fileDBOptional = fileDBRepository.findById(fileId);
        if(fileDBOptional.isEmpty())
            throw new ServiceException("No file for this id");

        FileDB fileDB = fileDBOptional.get();
        var optional = fileTagRepository.findById_FileId(fileDB.getId());
        if(optional.isEmpty()) {
            throw new Exception("empty File Tag");
        }
        List<FileTagEntity> entities = optional.get();
        List<String> tags = new ArrayList<>();
        for(FileTagEntity ft: entities) {
            tags.add(tagRepository.findById(ft.getId().getTagId()).get().getName());
        }
        String userId = fileRoomDBRepository.findById_FileIdAndId_RoomId(fileId, roomId).get(0).getId().getUserId();
        return fileDbMapper.toDto(fileDB, userRepository.findById(userId).get().getUsername(), tags);
    }


    /**
     * Get the whole file with its content
     * @param id - String
     * @return Optional<FileDB>
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
        logger.info("ENTER acceptFile, fileId : {} in room : {}", fileId, roomId);
        var f = fileDBRepository.findById(fileId);
        if (f.isEmpty()) {
            logger.info("EXIT exiting, file iwth id : {} not found", fileId);
            throw new FileException("File not found!");
        }
        var fileRoom = fileRoomDBRepository.findById_FileIdAndId_RoomId(fileId, roomId).get(0);
        if (fileRoom.isAccepted()) {
            logger.info("EXIT exiting, file with id : {} already accepted in room : {}", fileId, roomId);
            throw new FileException("Conflict with current state! File already accepted!");
        }
        fileRoomDBRepository.delete(fileRoom);
        fileRoom.Accept();
        fileRoomDBRepository.save(fileRoom);
        logger.info("EXIT acceptFile success");
    }

    /**
     * function that marks a file as denied
     *
     * @param fileId id of file to be denied
     * @return the modified FileDB entity
     */
    @Override
    public FileDB denyFile(String fileId, String roomId) {
        logger.info("ENTER denyFile fileId : {}, in room : {}", fileId, roomId);
        var f = fileDBRepository.findById(fileId).get();
        var fileRoomIdDB = fileRoomDBRepository.findById_FileIdAndId_RoomId(fileId, roomId).get(0);
        fileRoomDBRepository.delete(fileRoomIdDB);
        var fileRoomDBList = fileRoomDBRepository.findById_FileId(fileId);
        if (fileRoomDBList.size() == 0) {
            var fileTagList = fileTagRepository.findAllById_FileId(fileId);
            for(var fileTag : fileTagList){
               fileTagRepository.delete(fileTag);
}
            fileDBRepository.delete(f);
        }
        logger.info("EXIT denyFile");
        return null;
    }


    /**
     * Detects handwriting from given file
     * @param file the file that contains handwritten text
     * @param userId id of the user that sent the file
     * @return string containing the recognized data from the file
     * @throws IOException
     */
    @Override
    public String detectHandwriting(MultipartFile file, String userId) throws IOException {
        String TEMP_PATH = "src/main/java/com/noteit/noteit/hwrecognition/temp";
        Date date = new Date() ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss") ;
        String fileName = dateFormat.format(date) + ".jpeg";
        String path = TEMP_PATH + "/images/" + fileName;
        File localFile = new File(path);
        if (localFile.createNewFile()) {
            OutputStream os = Files.newOutputStream(Paths.get(path));
            os.write(file.getBytes());
            os.close();
            return TextDetector.detectDocumentText(path);
        } else throw new IOException("Could not process image file " + fileName);
    }

    /**
     * Removes file from given path
     * @param filePath path to the file that is deleted
     * @throws IOException
     */
    @Override
    public void removeFromTemp(String filePath) throws IOException {
        String[] pathComps = filePath.strip().split("/");
        String filename = pathComps[pathComps.length - 1];
        TextDetector.deleteFromTemp("files/" + filename);
    }

    /**
     * @param roomId id of room where the function search for all files
     * @return a stream of FileDbwRAPPER entity, containing data about all files in given roomId
     */
    @Override
    public Stream<FileDbWrapper> getWrappedFilesForRoom(String roomId) {
        logger.info("ENTER/EXIT getWrapperFilesForRoom");
        return fileRoomDBRepository.findById_RoomId(roomId).stream()
                .map(x -> {
                    var fileDb = fileDBRepository.findById(x.getId().getFileId()).get();
                    return new FileDbWrapper(fileDb.getName(), fileDb.getId(), fileDb.getType(), fileDb.getSize(), fileDb.getDate(), userRepository.findById(x.getId().getUserId()).get().getUsername(), x.getId().getApproved());
                });
    }

    /**
     * function that increases number of views for a file in a room
     * @param fileId id of file that function marks as viewed
     * @param roomId id of room where file was viewed
     */
    @Override
    public void fileViewed(String fileId, String roomId) {
        logger.info("ENTER fileViewed for fileId : {} in room {}", fileId, roomId);
        var fileRoom = fileRoomDBRepository.findById_FileIdAndId_RoomId(fileId, roomId).get(0);
        fileRoomDBRepository.delete(fileRoom);
        fileRoom.View();
        fileRoomDBRepository.save(fileRoom);
        logger.info("EXIT fileViewed");
    }

    /**
     * @param fileId id of a FileDB
     * @param roomId id of a Room
     * @return String representing id o user that posted file with fileId in room with roomId
     */
    @Override
    public String getUserIdByFileAndRoom(String fileId, String roomId) {
        logger.info("ENTER/EXIT getUserIdByFileAndRoom for fileId : {}, roomId : {}", fileId, roomId);
        return fileRoomDBRepository.findById_FileIdAndId_RoomId(fileId, roomId).get(0).getId().getUserId();
    }

    /**
     * Function that gets number of views and downloads for certain user and their files
     * @param userId
     * @return Integer representing the sum of views+downloads for every file uploaded by certain user
     */
    @Override
    public Integer getUserViewsAndDownloadsCount(String userId) {
        List<FileRoomDB> list = fileRoomDBRepository.findById_UserId(userId);
        int viewsAndDownloadsCount = 0;
        for (FileRoomDB elem :
                list) {
            viewsAndDownloadsCount += elem.getId().getViews();
            viewsAndDownloadsCount += elem.getId().getDownloads();
        }
        return viewsAndDownloadsCount;
      }

    public void downloadFile(String id, String roomId) throws FileException {
        logger.info("ENTER download, fileId : {} in room : {}", id, roomId);
        var f = fileDBRepository.findById(id);
        if (f.isEmpty()) {
            logger.info("EXIT exiting, file iwth id : {} not found", id);
            throw new FileException("File not found!");
        }
        var fileRoom = fileRoomDBRepository.findById_FileIdAndId_RoomId(id, roomId).get(0);

        fileRoomDBRepository.delete(fileRoom);
        fileRoom.Download();
        fileRoomDBRepository.save(fileRoom);
        logger.info("EXIT download success");

    }

    /**
     * function that obtains the most recent files from all the rooms a user belong to
     * @param token token of user
     * @param pageNumber a value that is used to determine the index of the last file added in the list of the most recent files
     * @return a list of FileRoomDto
     */
    @Override
    public List<FileRoomDto> getRecentFilesFromToken(String token, int pageNumber) {
        logger.info("ENTER getRecentFilesFromToken with user token: {} and pageNumber: {}", token, pageNumber);

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
                    if (fileRoomDB.isAccepted())
                    {
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
        }

        Collections.sort(fileRoomDtoList);

        for (int currentFileIndex = startFileIndex; currentFileIndex <= startFileIndex + filesPerPage - 1 && currentFileIndex < fileRoomDtoList.size(); currentFileIndex++) {
            currentFileRoomDtoList.add(fileRoomDtoList.get(currentFileIndex));
        }


        logger.info("EXIT getRecentFilesFromToken with succes, the list of files is: {}", currentFileRoomDtoList);
        return currentFileRoomDtoList;
    }

    /**
     * function that obtains the list of files that contains a given sequence in the filename
     * @param token token of user
     * @param filename filename of room
     * @return a list of FileRoomDto
     */
    @Override
    public List<FileRoomDto> getSearchedFilesFromName(String token, String filename) {
        logger.info("ENTER getSearchedFilesFromName with user token: {} and filename: {}", token, filename);

        UserEntity user = userRepository.findByToken(token);
        List<UserRoomEntity> userRoomEntities = userRoomRepository.findUserRoomEntityByUserRoomId_UserId(user.getId());

        List<FileRoomDto> fileRoomDtoList = new ArrayList<>();
        for (UserRoomEntity userRoomEntity : userRoomEntities) {
            String roomId = userRoomEntity.getUserRoomId().getRoomId();
            Optional<RoomEntity> room = roomRepository.findById(roomId);
            if (room.isPresent()) {
                List<FileRoomDB> fileRoomDBList = fileRoomDBRepository.findById_RoomId(room.get().getId());
                for (FileRoomDB fileRoomDB : fileRoomDBList) {
                    if (fileRoomDB.isAccepted()) {
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
        }

        logger.info("EXIT getSearchedFilesFromName with succes, the list of files is: {}", fileRoomDtoList);
        return fileRoomDtoList;
    }

    /**
     * function that obtains the list of files that contains a given tag
     * @param token token of user
     * @param tag tag of room
     * @return a list of FileRoomDto
     */
    @Override
    public List<FileRoomDto> getSearchedFilesFromTag(String token, String tag) {
        logger.info("ENTER getSearchedFilesFromTag with user token: {} and file tag: {}", token, tag);

        UserEntity user = userRepository.findByToken(token);
        List<UserRoomEntity> userRoomEntities = userRoomRepository.findUserRoomEntityByUserRoomId_UserId(user.getId());

        List<FileRoomDto> fileRoomDtoList = new ArrayList<>();
        for (UserRoomEntity userRoomEntity : userRoomEntities) {
            String roomId = userRoomEntity.getUserRoomId().getRoomId();
            Optional<RoomEntity> room = roomRepository.findById(roomId);
            if (room.isPresent()) {
                List<FileRoomDB> fileRoomDBList = fileRoomDBRepository.findById_RoomId(room.get().getId());
                for (FileRoomDB fileRoomDB : fileRoomDBList) {
                    if (fileRoomDB.isAccepted()) {
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
        }


        logger.info("EXIT getSearchedFilesFromTag with succes, the list of files is: {}", fileRoomDtoList);
        return fileRoomDtoList;
    }
}
