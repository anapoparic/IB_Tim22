package rs.ac.uns.ftn.asd.BookedUp.mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.dto.AccommodationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.HostDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.PhotoDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class AccommodationMapper {

    private static ModelMapper modelMapper;

    @Autowired
    public AccommodationMapper(ModelMapper modelMapper) {

        AccommodationMapper.modelMapper = modelMapper;

    }
    public static Accommodation toEntity(AccommodationDTO dto) {
        return modelMapper.map(dto, Accommodation.class);
    }

    public static AccommodationDTO toDto(Accommodation entity) {
        return modelMapper.map(entity, AccommodationDTO.class);
    }

    //HostMapper hostMapper = new HostMapper();

//    PhotoMapper photoMapper = new PhotoMapper();
//    @Override
//    public Accommodation toEntity(AccommodationDTO dto) {
//        if (dto == null){
//            return null;
//        }
//
//        List<Reservation> reservations = new ArrayList<Reservation>();
//        List<Review> reviews = new ArrayList<Review>();
//        //Host host = hostMapper.toEntity(dto.getHostDTO());
//        List<Photo> photos = new ArrayList<Photo>();
//        if (dto.getPhotos() != null){
//            for (PhotoDTO photoDTO : dto.getPhotos())
//                photos.add(photoMapper.toEntity(photoDTO));
//        }
//
//
//        return new Accommodation(dto.getId(),  dto.getName(), dto.getDescription(), dto.getAddress(), 0.0, dto.getMinGuests(),  dto.getMaxGuests(), 0, dto.isAutomaticReservationAcceptance(), dto.getStatus(), dto.getPriceType(),  dto.getType(), dto.getAmenities(), photos, dto.getAvailability(), dto.getPriceChanges(), reservations, reviews, 0.0);
//
//    }
//
//    @Override
//    public AccommodationDTO toDto(Accommodation entity) {
//        if (entity == null){
//            return null;
//        }
//        //HostDTO hostDTO = hostMapper.toDto(entity.getHost());
//        List<PhotoDTO> photoDTOS = new ArrayList<PhotoDTO>();
//        if (entity.getPhotos() != null){
//            for (Photo photo : entity.getPhotos())
//                photoDTOS.add(photoMapper.toDto(photo));
//        }
//        return new AccommodationDTO(entity.getId(), entity.getName(), entity.getDescription(), entity.getAddress(), entity.getAmenities(), photoDTOS, entity.getMinGuests(), entity.getMaxGuests(), entity.getType(), entity.getAvailability(), entity.getPriceType(), entity.getPriceChanges(), entity.isAutomaticReservationAcceptance(), entity.getStatus());
//    }




}
