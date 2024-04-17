package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.Accommodation;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.dto.AccommodationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.NotificationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.PhotoDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;
import rs.ac.uns.ftn.asd.BookedUp.domain.Notification;
import rs.ac.uns.ftn.asd.BookedUp.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public UserMapper(ModelMapper modelMapper) {

        UserMapper.modelMapper = modelMapper;

    }
    public static User toEntity(UserDTO dto) {
        return modelMapper.map(dto, User.class);
    }

    public static UserDTO toDto(User entity) {
        return modelMapper.map(entity, UserDTO.class);
    }
}



//    @Autowired
//    private ModelMapper modelMapper;
//
//    @Autowired
//    UserService userService;
//
//    @Override
//    public User toEntity(UserDTO dto) {
//        if (dto == null){
//            return null;
//        }
//
//        User user = modelMapper.map(dto, User.class);
//
//        if (dto.getId() != null) {
//            User oldUser = userService.getById(dto.getId());
//            user.setAuthority(oldUser.getAuthority());
//            user.setLastPasswordResetDate(oldUser.getLastPasswordResetDate());
//        }
//        return user;
//    }
//
//    @Override
//    public UserDTO toDto(User entity) {
//        if (entity == null){
//            return null;
//        }
//        return modelMapper.map(entity, UserDTO.class);
//    }

