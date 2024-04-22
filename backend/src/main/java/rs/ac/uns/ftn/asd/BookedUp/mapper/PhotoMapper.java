package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.Notification;
import rs.ac.uns.ftn.asd.BookedUp.domain.Photo;
import rs.ac.uns.ftn.asd.BookedUp.dto.NotificationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.PhotoDTO;

@Component
public class PhotoMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public PhotoMapper(ModelMapper modelMapper) {
        PhotoMapper.modelMapper = modelMapper;
    }
    public static Photo toEntity(PhotoDTO dto) {
        return modelMapper.map(dto, Photo.class);
    }

    public static PhotoDTO toDto(Photo entity) {
        return modelMapper.map(entity, PhotoDTO.class);
    }

//    @Override
//    public Photo toEntity(PhotoDTO dto) {
//        if (dto == null){
//            return null;
//        }
//
//        return new Photo(dto.getId(), dto.getUrl(), dto.getCaption(), 0, 0);
//    }
//
//    @Override
//    public PhotoDTO toDto(Photo entity) {
//        if (entity == null){
//            return null;
//        }
//        return new PhotoDTO(entity.getId(), entity.getUrl(), entity.getCaption());
//    }
}
