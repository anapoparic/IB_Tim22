package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.dto.*;

@Component
public class NotificationMapper  {

    private static ModelMapper modelMapper;

    @Autowired
    public NotificationMapper(ModelMapper modelMapper) {
        NotificationMapper.modelMapper = modelMapper;
    }
    public static Notification toEntity(NotificationDTO dto) {
        return modelMapper.map(dto, Notification.class);
    }

    public static NotificationDTO toDto(Notification entity) {
        return modelMapper.map(entity, NotificationDTO.class);
    }
//    @Autowired
//    UserMapper userMapper;
//
//    @Override
//    public Notification toEntity(NotificationDTO dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        User fromUser = userMapper.toEntity(dto.getFromUserDTO());
//        User toUser = userMapper.toEntity((dto.getToUserDTO()));
//
//        Notification notification = new Notification();
//        notification.setId(dto.getId());
//        notification.setFrom(fromUser);
//        notification.setTo(toUser);
//        notification.setMessage(dto.getMessage());
//        notification.setTitle(dto.getTitle());
//        notification.setTimestamp(dto.getTimestamp());
//        notification.setType(dto.getType());
//
//        return notification;
//    }
//
//    @Override
//    public NotificationDTO toDto(Notification entity) {
//        if (entity == null) {
//            return null;
//        }
//
//        UserDTO fromUserDTO = userMapper.toDto(entity.getFrom());
//        UserDTO toUserDTO = userMapper.toDto(entity.getTo());
//
//        NotificationDTO notificationDTO = new NotificationDTO();
//        notificationDTO.setId(entity.getId());
//        notificationDTO.setFromUserDTO(fromUserDTO);
//        notificationDTO.setToUserDTO(toUserDTO);
//        notificationDTO.setMessage(entity.getMessage());
//        notificationDTO.setTitle(entity.getTitle());
//        notificationDTO.setTimestamp(entity.getTimestamp());
//        notificationDTO.setType(entity.getType());
//
//        return notificationDTO;
//    }
}
