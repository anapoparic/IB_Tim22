package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.Accommodation;
import rs.ac.uns.ftn.asd.BookedUp.domain.Guest;
import rs.ac.uns.ftn.asd.BookedUp.domain.Reservation;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.dto.AccommodationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.ReservationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;

@Component
public class ReservationMapper  {

    private static ModelMapper modelMapper;

    @Autowired
    public ReservationMapper(ModelMapper modelMapper) {
        ReservationMapper.modelMapper = modelMapper;
    }
    public static Reservation toEntity(ReservationDTO dto) {
        return modelMapper.map(dto, Reservation.class);
    }

    public static ReservationDTO toDto(Reservation entity) {
        return modelMapper.map(entity, ReservationDTO.class);
    }

//    AccommodationMapper accommodationMapper = new AccommodationMapper();
//
//    @Override
//    public Reservation toEntity(ReservationDTO dto) {
//        if (dto == null) {
//            return null;
//        }
//
//        Accommodation accommodation = accommodationMapper.toEntity(dto.getAccommodationDTO());
//
//        Reservation reservation = new Reservation();
//        reservation.setId(dto.getId());
//        reservation.setAccommodation(accommodation);
//        reservation.setStartDate(dto.getStartDate());
//        reservation.setEndDate(dto.getEndDate());
//        reservation.setGuestsNumber(dto.getGuestsNumber());
//        reservation.setStatus(dto.getStatus());
//        reservation.setCreatedTime(null);
//        reservation.setTotalPrice(0);
//        reservation.setGuest(new Guest());
//
//        return reservation;
//    }
//
//    @Override
//    public ReservationDTO toDto(Reservation entity) {
//        if (entity == null) {
//            return null;
//        }
//
//        AccommodationDTO accommodationDTO = accommodationMapper.toDto(entity.getAccommodation());
//
//        ReservationDTO reservationDTO = new ReservationDTO();
//        reservationDTO.setId(entity.getId());
//        reservationDTO.setAccommodationDTO(accommodationDTO);
//        reservationDTO.setStartDate(entity.getStartDate());
//        reservationDTO.setEndDate(entity.getEndDate());
//        reservationDTO.setGuestsNumber(entity.getGuestsNumber());
//        reservationDTO.setStatus(entity.getStatus());
//
//        return reservationDTO;
//    }
}
