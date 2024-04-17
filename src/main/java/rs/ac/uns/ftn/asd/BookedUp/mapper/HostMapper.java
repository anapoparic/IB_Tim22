package rs.ac.uns.ftn.asd.BookedUp.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.dto.*;

@Component
public class HostMapper{

    private static ModelMapper modelMapper;

    @Autowired
    public HostMapper(ModelMapper modelMapper) {
        HostMapper.modelMapper = modelMapper;
    }
    public static Host toEntity(HostDTO dto) {
        return modelMapper.map(dto, Host.class);
    }

    public static HostDTO toDto(Host entity) {
        return modelMapper.map(entity, HostDTO.class);
    }

//    AccommodationMapper accommodationMapper = new AccommodationMapper();
//
//    ReservationMapper reservationMapper = new ReservationMapper();
//
//    NotificationMapper notificationMapper = new NotificationMapper();
//
//    StatisticsMapper statisticsMapper = new StatisticsMapper();
//
//    AccommodationStatisticsMapper accommodationStatisticsMapper = new AccommodationStatisticsMapper();
//
//    PhotoMapper photoMapper = new PhotoMapper();
//    @Override
//    public Host toEntity(HostDTO dto) {
//        if (dto == null) {
//            return null;
//        }
//        List<Accommodation> accommodations = new ArrayList<Accommodation>();
//        if(dto.getAccommodations() != null) {
//            for(AccommodationDTO accommodationDTO : dto.getAccommodations())
//                accommodations.add(accommodationMapper.toEntity(accommodationDTO));
//        }
//
//        List<Reservation> requests = new ArrayList<Reservation>();
//        if(dto.getRequests() != null) {
//            for(ReservationDTO reservationDTO : dto.getRequests())
//                requests.add(reservationMapper.toEntity(reservationDTO));
//        }
//
//        List<Notification> notifications = new ArrayList<Notification>();
//        if(dto.getNotifications() != null) {
//            for(NotificationDTO notificationDTO : dto.getNotifications())
//                notifications.add(notificationMapper.toEntity(notificationDTO));
//        }
//
//        List<Statistics> statistics = new ArrayList<Statistics>();
//        if(dto.getStatistics() != null) {
//            for(StatisticsDTO statisticsDTO : dto.getStatistics())
//                statistics.add(statisticsMapper.toEntity(statisticsDTO));
//        }
//
//        List<AccommodationStatistics> accommodationStatistics = new ArrayList<AccommodationStatistics>();
//        if(dto.getAccommodationStatistics() != null) {
//            for(AccommodationStatisticsDTO accommodationStatisticsDTO : dto.getAccommodationStatistics())
//                accommodationStatistics.add(accommodationStatisticsMapper.toEntity(accommodationStatisticsDTO));
//        }
//
//        Photo photo = photoMapper.toEntity(dto.getProfilePicture());
//
//        return new Host(dto.getId(), dto.getFirstName(), dto.getLastName(), dto.getAddress(), dto.getPhone(), dto.getEmail(), dto.getPassword(), dto.isBlocked(), dto.isVerified(), photo, null, null, notifications, dto.getAverageRating(), accommodations, requests, statistics, accommodationStatistics, dto.isReservationCreatedNotificationEnabled(), dto.isCancellationNotificationEnabled(), dto.isHostRatingNotificationEnabled(), dto.isAccommodationRatingNotificationEnabled());
//
//    }
//
//    @Override
//    public HostDTO toDto(Host entity) {
//        if (entity == null) {
//            return null;
//        }
//        List<AccommodationDTO> accommodationDTOS = new ArrayList<AccommodationDTO>();
//        if(entity.getAccommodations() != null) {
//            for(Accommodation accommodation : entity.getAccommodations())
//                accommodationDTOS.add(accommodationMapper.toDto(accommodation));
//        }
//
//        List<ReservationDTO> requestsDTOS = new ArrayList<ReservationDTO>();
//        if(entity.getRequests() != null) {
//            for(Reservation reservation : entity.getRequests())
//                requestsDTOS.add(reservationMapper.toDto(reservation));
//        }
//
//        List<NotificationDTO> notificationDTOS = new ArrayList<NotificationDTO>();
//        if(entity.getNotifications() != null) {
//            for(Notification notification : entity.getNotifications())
//                notificationDTOS.add(notificationMapper.toDto(notification));
//        }
//
//        List<StatisticsDTO> statisticsDTOS = new ArrayList<StatisticsDTO>();
//        if(entity.getStatistics() != null) {
//            for(Statistics statistics : entity.getStatistics())
//                statisticsDTOS.add(statisticsMapper.toDto(statistics));
//        }
//
//        List<AccommodationStatisticsDTO> accommodationStatisticsDTOS = new ArrayList<AccommodationStatisticsDTO>();
//        if(entity.getAccommodationStatistics() != null) {
//            for(AccommodationStatistics accommodationStatistics : entity.getAccommodationStatistics())
//                accommodationStatisticsDTOS.add(accommodationStatisticsMapper.toDto(accommodationStatistics));
//        }
//
//        PhotoDTO photoDTO = photoMapper.toDto(entity.getProfilePicture());
//
//        return new HostDTO(entity.getId(), entity.getFirstName(), entity.getLastName(), entity.getAddress(), entity.getPhone(), entity.getEmail(), entity.getPassword(), entity.isBlocked(), entity.isVerified(), photoDTO, notificationDTOS, entity.getAverageRating(), accommodationDTOS, requestsDTOS, statisticsDTOS, accommodationStatisticsDTOS, entity.isReservationCreatedNotificationEnabled(), entity.isCancellationNotificationEnabled(), entity.isHostRatingNotificationEnabled(), entity.isAccommodationRatingNotificationEnabled());
//
//    }
}
