package com.noteit.noteit.files.service;

import com.noteit.noteit.entities.FileTagEntity;
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

    @Override
    public FileDB add(FileDB fileDB) {
        return fileDBRepository.save(fileDB);
    }

    @Override
    public FileRoomDB addFileRoom(FileRoomDB fileRoomDB) {
        return fileRoomDBRepository.save(fileRoomDB);
    }

    @Override
    public List<FileRoomDB> findByRoomId(String roomId) {
        return fileRoomDBRepository.findById_RoomId(roomId);
    }

    @Override
    @Transactional
    public FileDB store(MultipartFile file, String userId, String roomId) throws IOException, FileException {
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
            return saved;
        }
    }

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
}
