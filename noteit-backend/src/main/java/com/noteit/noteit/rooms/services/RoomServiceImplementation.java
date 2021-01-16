package com.noteit.noteit.rooms.services;

import com.noteit.noteit.files.model.FileTagEntity;
import com.noteit.noteit.files.repository.FileTagRepository;
import com.noteit.noteit.rooms.dtos.RoomDto;
import com.noteit.noteit.rooms.model.UserRoomEntity;
import com.noteit.noteit.rooms.repository.RoomRepository;
import com.noteit.noteit.rooms.repository.UserRoomRepository;
import com.noteit.noteit.rooms.services.RoomServiceInterface;
import com.noteit.noteit.tags.repository.TagRepository;
import com.noteit.noteit.users.model.UserEntity;
import com.noteit.noteit.files.model.FileRoomDB;
import com.noteit.noteit.files.repository.FileRoomDBRepository;
import com.noteit.noteit.rooms.model.RoomEntity;
import com.noteit.noteit.tags.model.TagEntity;
import com.noteit.noteit.users.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RoomServiceImplementation implements RoomServiceInterface {
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private UserRoomRepository userRoomRepository;
    private FileRoomDBRepository fileRoomRepository;
    private FileTagRepository fileTagRepository;
    private TagRepository tagRepository;

    /**
     * function that returns all the rooms
     * @return a RoomDto
     */
    @Override
    public List<RoomDto> getRooms() {
        List<RoomDto> roomDtos = new ArrayList<>();
        for (RoomEntity roomEntity : roomRepository.findAll()) {
            RoomDto roomDto = new RoomDto(roomEntity.getId(), roomEntity.getName());
            roomDtos.add(roomDto);
        }

        return roomDtos;
    }

    /**
     * function that obtains all the rooms a user belong to
     * @param token token of user
     * @return a list of RoomDto
     */
    @Override
    public List<RoomDto> getRoomsByToken(String token) {
        List<RoomDto> roomDtos = new ArrayList<>();
        String ownerId = userRepository.findByToken(token).getId();

        List<UserRoomEntity> userRoomEntities = userRoomRepository.findUserRoomEntityByUserRoomId_UserId(ownerId);
        for (UserRoomEntity userRoomEntity : userRoomEntities)
        {
            Optional<RoomEntity> roomEntity = roomRepository.findById(userRoomEntity.getUserRoomId().getRoomId());
            if (roomEntity.isPresent())
            {
                RoomDto roomDto = new RoomDto(roomEntity.get().getId(), roomEntity.get().getName());
                roomDtos.add(roomDto);
            }
        }

        return roomDtos;
    }

    /**
     * function that creates a new room
     * @param name name of futere room
     * @param token token of user
     * @return the created room
     */
    @Override
    public RoomDto createRoom(String name, String token) {
        String ownerId = userRepository.findByToken(token).getId();
        RoomEntity roomEntity = new RoomEntity(name, ownerId);
        roomRepository.save(roomEntity);
        UserRoomEntity userRoom = new UserRoomEntity(ownerId, roomEntity.getId());
        userRoomRepository.save(userRoom);
        return new RoomDto(roomEntity.getId(), name);
    }

    @Override
    public RoomEntity getById(String id) {
        Optional<RoomEntity> roomEntity = roomRepository.findById(id);
        if (roomEntity.isPresent())
            return new RoomEntity(roomEntity.get().getId(), roomEntity.get().getName(), roomEntity.get().getOwnerId());
        return null;
    }

    @Override
    public List<RoomEntity> getByName(String name) {
        List<RoomEntity> rooms=new ArrayList<>();
       Optional<List<RoomEntity>> roomsList=roomRepository.findByNameContains(name);
       if(roomsList.isPresent())
           for(var room: roomsList.get())
               rooms.add(new RoomEntity(room.getId(), room.getName(), room.getOwnerId()));
       return rooms;
    }

    @Override
    public List<RoomEntity> filterRooms(List<String> tagNames) {
        List<TagEntity> tags=new ArrayList<>();
        for(var name:tagNames){
            TagEntity tag=tagRepository.findByName(name);
            if(tag!=null)
                tags.add(tag);
        }

        List<FileTagEntity> fileTags=new ArrayList<>();
        for(var tag:tags){
            Optional<List<FileTagEntity>> fileTagsWithTag=fileTagRepository.findById_TagId(tag.getId());
            if(fileTagsWithTag.isPresent())
                fileTags.addAll(fileTagsWithTag.get());
        }
        List<FileRoomDB> fileRooms=new ArrayList<>();
        for(var fileTag:fileTags){
            List<FileRoomDB> fileRoomsWithFileTag=fileRoomRepository.findById_FileId(fileTag.getId().getFileId());
            fileRooms.addAll(fileRoomsWithFileTag);
        }

        List<RoomEntity> rooms=new ArrayList<>();
        for(var fileRoom:fileRooms){
            Optional<RoomEntity> room=roomRepository.findById(fileRoom.getId().getRoomId());
            if(room.isPresent())
                if(!rooms.contains(room.get()))
                    rooms.add(room.get());
        }
        return rooms;
    }

    /**
     * function that checks if an user is the owner of a room
     * @param token token of user
     * @param roomId id of room
     * @return a JSON string with the answer
     */
    @Override
    public String checkIfIsAdmin(String token, String roomId) {
        RoomEntity roomEntity = roomRepository.findById(roomId).get();
        UserEntity userEntity = userRepository.findByToken(token);
        if (roomEntity != null && userEntity != null)
        {
            if (roomEntity.getOwnerId().equals(userEntity.getId()))
            {
                return "[{" + '"' + "isAdmin" + '"' + ':' + '"' + "true" + '"' + "}]";
            }
        }
        return "[{" + '"' + "isAdmin" + '"' + ':' + '"' + "false" + '"' + "}]";
    }

    /**
     * function that adds an user to a room
     * @param token token of user
     * @param roomId id of room
     */
    @Override
    public void joinRoom(String token, String roomId)
    {
        String ownerId = userRepository.findByToken(token).getId();
        UserRoomEntity userRoom = new UserRoomEntity(ownerId, roomId);
        userRoomRepository.save(userRoom);
    }
}
