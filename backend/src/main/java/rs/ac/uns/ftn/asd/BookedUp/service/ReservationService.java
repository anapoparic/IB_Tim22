package rs.ac.uns.ftn.asd.BookedUp.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.BookedUp.domain.Accommodation;
import rs.ac.uns.ftn.asd.BookedUp.domain.Reservation;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.ReservationStatus;
import rs.ac.uns.ftn.asd.BookedUp.repository.IReservationRepository;
import rs.ac.uns.ftn.asd.BookedUp.service.interfaces.ServiceInterface;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class ReservationService implements ServiceInterface<Reservation> {
    @Autowired
    private IReservationRepository repository;

    @Autowired
    private AccommodationService accommodationService ;
    @Override
    public Collection<Reservation> getAll() {
        return repository.findAll();
    }

    @Override
    public Reservation getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Reservation create(Reservation reservation) throws Exception {
        LocalDateTime createdTime = LocalDateTime.now();
        createdTime = createdTime.withSecond(0).withNano(0);
        reservation.setCreatedTime(createdTime);
        if (reservation.getId() != null) {
            throw new Exception("Id mora biti null prilikom perzistencije novog entiteta.");
        }
        if (reservation.getStatus().equals(ReservationStatus.ACCEPTED)) {
            accommodationService.updateAvailibility(reservation.getAccommodation(), reservation.getStartDate(), reservation.getEndDate());
        }
        return repository.save(reservation);
    }

    @Override
    public Reservation save(Reservation reservation) throws Exception {
        return repository.save(reservation);
    }

    public List<Reservation> findAllByGuestId(Long id){
        return repository.findAllByGuestId(id);
    }

    public List<Reservation> findAllByHostId(Long id){
        return repository.findAllByHostId(id);
    }
    public List<Reservation> findAllCreatedByHostId(Long id){
        return repository.findAllCreatedByHostId(id);
    }
    public List<Reservation> findAllAcceptedByHostId(Long id){
        return repository.findAllAcceptedByHostId(id);
    }
    public List<Reservation> findAllRejectedByHostId(Long id){
        return repository.findAllRejectedByHostId(id);
    }
    public List<Reservation> findAllCompletedByHostId(Long id){
        return repository.findAllCompletedByHostId(id);
    }
    public List<Reservation> findAllCancelledByHostId(Long id){
        return repository.findAllCancelledByHostId(id);
    }

    public List<Reservation> findAllByAccommodationId(Long id){
        return repository.findAllByAccommodationId(id);
    }

    public List<Reservation> findReservtaionsByStatusAndGuestId(Long id){
        return repository.findAllByAccommodationId(id);
    }


    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Reservation> getOverlappingReservations(Reservation reservation) {
        Date startDate = reservation.getStartDate();
        Date endDate = reservation.getEndDate();

        List<Reservation> allReservations = new ArrayList<>(repository.findAll());
        List<Reservation> overlappingReservations = new ArrayList<>();

        for (Reservation existingReservation : allReservations) {
            if (!existingReservation.getId().equals(reservation.getId())) {
                Date existingStartDate = existingReservation.getStartDate();
                Date existingEndDate = existingReservation.getEndDate();
                boolean overlap = false;
                if (existingReservation.getStatus().equals(ReservationStatus.CREATED)) {
                    overlap = (existingStartDate.compareTo(startDate) >= 0 && existingStartDate.compareTo(endDate) <= 0) || (existingEndDate.compareTo(startDate) >= 0 && existingEndDate.compareTo(endDate) <= 0);
                }

                if (overlap ) {
                    overlappingReservations.add(existingReservation);
                }
            }
        }

        return overlappingReservations;
    }

    public void cancelReservation(Reservation reservation) throws Exception{
        Reservation reservationToUpdate = repository.findById(reservation.getId()).orElse(null);
        if (reservationToUpdate == null) {
            throw new Exception("Trazeni entitet nije pronadjen.");
        }

        if(reservationToUpdate.getStatus() == ReservationStatus.ACCEPTED){
            Accommodation accommodation = reservationToUpdate.getAccommodation();

            accommodationService.addAvailability(accommodation, reservationToUpdate.getStartDate(), reservationToUpdate.getEndDate());
        }

        reservationToUpdate.setStatus(ReservationStatus.CANCELLED);
        repository.save(reservationToUpdate);
    }

    public void approveReservation(Reservation reservation) throws Exception {
        reservation.setStatus(ReservationStatus.ACCEPTED);
        List<Reservation> overlapping = getOverlappingReservations(reservation);
        for (Reservation res : overlapping){
            rejectReservation(res);
        }
        accommodationService.updateAvailibility(reservation.getAccommodation(), reservation.getStartDate(), reservation.getEndDate());
        repository.save(reservation);
    }

    public void rejectReservation(Reservation reservation) {
        reservation.setStatus(ReservationStatus.REJECTED);
        repository.save(reservation);
    }

    public List<Reservation> getReservationsByStatusAndGuestId(Long guestId, ReservationStatus reservationStatus) {
        return repository.getReservationsByStatusAndGuestId(guestId, reservationStatus);
    }

    public List<Reservation> getReservationsByGuestId(Long guestId) {
        return repository.getReservationsByGuestId(guestId);
    }

    public List<Reservation> getReservationsByStatusAndHostId(Long hostId, ReservationStatus reservationStatus) {
        return repository.getReservationsByStatusAndHostId(hostId, reservationStatus);
    }

    public List<Reservation> getReservationsByHostId(Long hostId) {
        return repository.getReservationsByHostId(hostId);
    }

    public void rejectReservationsForGuest(Long userId) {
        List<Reservation> reservations = repository.getReservationsByGuestId(userId);

        for (Reservation reservation : reservations) {
            if (ReservationStatus.ACCEPTED.equals(reservation.getStatus()) || ReservationStatus.CREATED.equals(reservation.getStatus())) {
                reservation.setStatus(ReservationStatus.REJECTED);
                repository.save(reservation);
            }
        }
    }

    @Scheduled(cron = "0 0 15 * * ?") // expression triggers the method every day at 15:00
    public void checkReservations() {
        List<Reservation> reservations = repository.findAll();
        Date today = new Date();

        for (Reservation reservation : reservations) {
            ReservationStatus status = reservation.getStatus();

            if (status.equals(ReservationStatus.ACCEPTED)) {
                // Check if the end date has passed
                if (!reservation.getEndDate().after(today)) {
                    System.out.println("Reservation ID COMPLETED " + reservation.getId());
                    // Your logic for handling accepted reservations with end date passed
                    reservation.setStatus(ReservationStatus.COMPLETED);
                }
            } else if (status.equals(ReservationStatus.CREATED)) {
                if (!reservation.getEndDate().after(today)) {
                    System.out.println("Reservation ID REJECTED " + reservation.getId());
                    reservation.setStatus(ReservationStatus.REJECTED);
                }
            }
        }
        repository.saveAll(reservations);
    }

    @PostConstruct
    public void onStartup() {
        System.out.println("Checking reservations on startup");
        checkReservations();
    }
}
