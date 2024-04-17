package rs.ac.uns.ftn.asd.BookedUp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.BookedUp.domain.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.ReservationStatus;
import rs.ac.uns.ftn.asd.BookedUp.repository.*;
import rs.ac.uns.ftn.asd.BookedUp.service.interfaces.ServiceInterface;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class HostService implements ServiceInterface<Host> {
    @Autowired
    private IHostRepository repository;

    @Autowired
    private IPhotoRepository photoRepository;

    @Autowired
    private IAccommodationRepository accommodationRepository;
    @Autowired
    private INotificationRepository notificationRepository;


    @Autowired
    private ReviewService reviewService;

    @Autowired
    private AccommodationService accommodationService;
    @Override
    public Collection<Host> getAll() {
        return repository.findAllHosts();
    }

    @Override
    public Host getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Host create(Host host) throws Exception {
        if (host.getId() != null) {
            throw new Exception("Id mora biti null prilikom perzistencije novog entiteta.");
        }
        return repository.save(host);
    }

    @Override
    public Host save(Host host) throws Exception {
        return repository.save(host);
    }

//    @Override
//    public HostDTO update(HostDTO hostDto) throws Exception {
//        Host host = hostMapper.toEntity(hostDto);
//        Host hostToUpdate = repository.findById(hostDto.getId()).orElse(null);
//        if (hostToUpdate == null) {
//            throw new Exception("Trazeni entitet nije pronadjen.");
//        }
//        hostToUpdate.setFirstName(host.getFirstName());
//        hostToUpdate.setLastName(host.getLastName());
//        hostToUpdate.setAddress(host.getAddress());
//        hostToUpdate.setEmail(host.getEmail());
//        hostToUpdate.setPassword(host.getPassword());
//        hostToUpdate.setPhone(host.getPhone());
//        hostToUpdate.setVerified(host.isVerified());
//        hostToUpdate.setProfilePicture(host.getProfilePicture());
//        hostToUpdate.setLastPasswordResetDate(host.getLastPasswordResetDate());
//        hostToUpdate.setBlocked(host.isBlocked());
//        hostToUpdate.setAverageRating(host.getAverageRating());
//        hostToUpdate.setAccommodations(host.getAccommodations());
//        hostToUpdate.setNotifications(host.getNotifications());
//        hostToUpdate.setRequests(host.getRequests());
//        hostToUpdate.setReservationCreatedNotificationEnabled(host.isReservationCreatedNotificationEnabled());
//        hostToUpdate.setCancellationNotificationEnabled(host.isCancellationNotificationEnabled());
//        hostToUpdate.setHostRatingNotificationEnabled(host.isHostRatingNotificationEnabled());
//        hostToUpdate.setAccommodationRatingNotificationEnabled(host.isAccommodationRatingNotificationEnabled());
//
//        hostToUpdate.setAuthority(host.getAuthority());
//        hostToUpdate.setProfilePicture(host.getProfilePicture());
//        hostToUpdate.setVerified(host.isVerified());
//        hostToUpdate.setLastPasswordResetDate(host.getLastPasswordResetDate());
//
//        Host updatedHost = repository.save(hostToUpdate);
//        return hostMapper.toDto(updatedHost);
//    }

    @Override
    public void delete(Long id) throws Exception {
        Host host = repository.findById(id).orElse(null);

        if (host == null)
            throw new Exception("Host doesn't exist");

        if (hasActiveReservations(host.getId())) {
            throw new Exception("Host has future reservations and cannot be deleted");
        }


        Address address = host.getAddress();
        if(address != null){
            address.setActive(false);
        }

        Photo profilePhoto = host.getProfilePicture();
        if(profilePhoto != null){
            profilePhoto.setActive(false);
            photoRepository.save(profilePhoto);
        }

        List<Accommodation> accommodations = accommodationService.findAllByHostId(id); //ko zna sta je ovo
        if(!accommodations.isEmpty()) {
            for (Accommodation accommodation : accommodations) {
                accommodation.setActive(false);
                accommodationRepository.save(accommodation);
            }
        }


        host.setActive(false);
        repository.save(host);
    }

    private boolean hasActiveReservations(Long id) {
        List<Accommodation> accommodations = accommodationService.findAllActiveByHostId(id); //ko zna

        if (accommodations != null) {
            for (Accommodation accommodation : accommodations) {
                List<Reservation> reservations = accommodation.getReservations();

                if (reservations != null) {
                    for (Reservation reservation : reservations) {
                        // Provera da li je rezervacija u budućnosti i da li je aktivna
                        if (reservation.getStartDate().after(new Date())
                                && reservation.getStatus() != ReservationStatus.CANCELLED
                                && reservation.getStatus() != ReservationStatus.COMPLETED
                                && reservation.getStatus() != ReservationStatus.REJECTED) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isWithinDateRange(Date date, Date fromDate, Date toDate) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localFromDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localToDate = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return !localDate.isBefore(localFromDate) && !localDate.isAfter(localToDate);
    }
    public Collection<Guest> getGuestsByHostId(Long hostId) throws Exception {
        Host host = repository.findById(hostId).orElse(null);

        if (host == null) {
            throw new Exception("Host doesn't exist");
        }

        return accommodationRepository.findGuestsByHostWithCompletedOrActiveReservations(host);
    }


    public void calculateAndSaveAverageRating(Long id) throws Exception {
        Host host = repository.findById(id).orElse(null);

        if (host == null)
            throw new Exception("Host doesn't exist");

        List<Review> reviews = reviewService.findAllActiveByHostId(id);

        double sumOfRatings = 0.0;
        int numberOfReviews = reviews.size();

        for (Review review : reviews) {
            sumOfRatings += review.getReview();
        }

        double averageRating = (numberOfReviews > 0) ? (sumOfRatings / numberOfReviews) : 0.0;

        // Setujte prosečnu ocenu u vašem smeštaju
        host.setAverageRating(averageRating);
        repository.save(host);
    }
}
