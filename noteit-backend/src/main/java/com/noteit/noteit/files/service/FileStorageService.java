package com.noteit.noteit.files.service;

import com.noteit.noteit.entities.FileTagEntity;
import com.noteit.noteit.entities.FileTagPK;
import com.noteit.noteit.entities.TagEntity;
import com.noteit.noteit.files.dtos.FileDbDto;
import com.noteit.noteit.files.exception.FileException;
import com.noteit.noteit.files.mapper.FileDbMapper;
import com.noteit.noteit.files.model.FileDB;
import com.noteit.noteit.files.model.FileRoomCompositePK;
import com.noteit.noteit.files.model.FileRoomDB;
import com.noteit.noteit.files.repository.FileDBRepository;
import com.noteit.noteit.files.repository.FileRoomDBRepository;
import com.noteit.noteit.repositories.FileTagRepository;
import com.noteit.noteit.repositories.TagRepository;
import com.noteit.noteit.repositories.UserRepository;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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

    private FileDbMapper fileDbMapper = new FileDbMapper();


    @Override
    public FileDB getById(String id) {
        return fileDBRepository.findById(id).get();
    }

    /**
     * @param file file to be saved
     * @param userId id of user that sent the file
     * @param roomId id of room where we save the file
     * @param tags string containing the tags given for file
     * @return saved FileDB
     * @throws IOException
     * @throws FileException if file is already in current room
     */
    @Override
    @Transactional
    public FileDB store(MultipartFile file, String userId, String roomId, String tags) throws IOException, FileException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Integer size = file.getBytes().length;
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
                fileRoomDBRepository.save(new FileRoomDB(new FileRoomCompositePK(roomId, cautat.getId())));
                saveTags(tags, cautat.getId());
                return cautat;
            }
        } else {
            //nu exista fisierul in BD
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
            Date date = new Date(System.currentTimeMillis());
            String data = formatter.format(date);
            FileDB fileDB = new FileDB(fileName, file.getContentType(), file.getBytes(), data, userId);
            FileDB saved = fileDBRepository.save(fileDB);
            FileRoomDB f = new FileRoomDB(new FileRoomCompositePK(roomId, saved.getId()));
            fileRoomDBRepository.save(f);
            saveTags(tags, saved.getId());
            return saved;
        }

    }


    /**
     * @param tags string that contains tags separated with ,
     * @param fileId id of file that contains given tags
     */
    private void saveTags(String tags, String fileId){
        String[] elems = tags.strip().split(",");
        for (String tag : elems){
            if (tagRepository.findByName(tag) == null){
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
        Optional<FileDB> fileDBOptional = fileDBRepository.findById(id);
        if(fileDBOptional.isEmpty())
            throw new ServiceException("No file for this id");

        FileDB fileDB = fileDBOptional.get();
        List<FileTagEntity> entities = fileTagRepository.findById_FileId(fileDB.getId()).get();
        List<String> tags = new ArrayList<>();
        for(FileTagEntity ft: entities) {
            tags.add(tagRepository.findById(ft.getId().getTagId()).get().getName());
        }
        return fileDbMapper.toDto(fileDB, userRepository.findById(fileDB.getUser_id()).get().getUsername(), tags);
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
     * @param fileId id of file to accepted
     * @return the modified FileDB entitu
     */
    @Override
    public FileDB acceptFile(String fileId) {
        var f = fileDBRepository.findById(fileId).get();
        if (f.getApproved().equals(1)) {
            return null;
        }
        f.setApproved(1);
        var a = fileDBRepository.save(f);
        return a;
    }

    /**
     * function that marks a file as denied
     * @param fileId id of file to be denied
     * @return the modified FileDB entity
     */
    @Override
    public FileDB denyFile(String fileId) {
        var f = fileDBRepository.findById(fileId).get();
        var fileRoomDBList = fileRoomDBRepository.findById_FileId(fileId);
        for (var fr: fileRoomDBList){
            fileRoomDBRepository.delete(fr);
        }
        fileDBRepository.delete(f);
        return null;
    }
}
