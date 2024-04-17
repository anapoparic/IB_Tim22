package rs.ac.uns.ftn.asd.BookedUp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.asd.BookedUp.domain.Guest;
import rs.ac.uns.ftn.asd.BookedUp.domain.Host;
import rs.ac.uns.ftn.asd.BookedUp.domain.Notification;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.NotificationType;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.Role;
import rs.ac.uns.ftn.asd.BookedUp.repository.IGuestRepository;
import rs.ac.uns.ftn.asd.BookedUp.repository.IHostRepository;
import rs.ac.uns.ftn.asd.BookedUp.repository.INotificationRepository;
import rs.ac.uns.ftn.asd.BookedUp.service.interfaces.ServiceInterface;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class NotificationService implements ServiceInterface<Notification> {
    @Autowired
    private INotificationRepository repository;

    @Autowired
    private IHostRepository hostRepository;
    @Autowired
    private IGuestRepository guestRepository;
    @Autowired
    private HostService hostService;
    @Autowired
    private GuestService guestService;

    @Override
    public Collection<Notification> getAll() {
        return repository.findAll();
    }

    @Override
    public Notification getById(Long id) {
        return repository.findById(id).orElse(null);
    }


    @Override
    public Notification create(Notification notification) throws Exception {
        if (notification.getId() != null) {
            throw new Exception("Id mora biti null prilikom perzistencije novog entiteta.");
        }
        return repository.save(notification);
    }

    @Override
    public Notification save(Notification notification) throws Exception {
        return repository.save(notification);
    }


//    @Override
//    public NotificationDTO update(NotificationDTO notificationDTO) throws Exception {
//        Notification notification = notificationMapper.toEntity(notificationDTO);
//        Notification notificationToUpdate= repository.findById(notification.getId()).orElse(null);
//        if (notificationToUpdate == null) {
//            throw new Exception("Trazeni entitet nije pronadjen.");
//        }
//        notificationToUpdate.setFrom(notification.getFrom());
//        notificationToUpdate.setTo(notification.getTo());
//        notificationToUpdate.setTitle(notification.getTitle());
//        notificationToUpdate.setMessage(notification.getMessage());
//        notificationToUpdate.setTimestamp(notification.getTimestamp());
//        notificationToUpdate.setType(notification.getType());
//        notificationToUpdate.setActive(notification.isActive());
//
//
//        Notification updatedNotification = repository.save(notificationToUpdate);
//        return notificationMapper.toDto(updatedNotification);
//    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Collection<Notification> getByUserId(Long id){
        Collection<Notification> notifications = getAll();
        Collection<Notification> results = new ArrayList<>();

        for (Notification notification : notifications) {
            if (notification.getTo() != null && notification.getTo().getId().equals(id)) {
                results.add(notification);
            }
        }

        return results;

    }

    public Collection<Notification> getEnabledByUserId(Long id){
        Collection<Notification> notifications = getAll();
        Collection<Notification> results = new ArrayList<>();

        for (Notification notification : notifications) {
            if (notification.getTo() != null && notification.getTo().getId().equals(id)) {
                if(notification.getTo().getRole() == Role.GUEST){
                    Guest guest = this.guestService.getById(notification.getTo().getId());

                    if((notification.getType() == NotificationType.RESERVATION_REQUEST_RESPONSE) && guest.isNotificationEnable()){
                        results.add(notification);
                    }
                }else if(notification.getTo().getRole() == Role.HOST ){
                    Host host = this.hostService.getById(notification.getTo().getId());

                    if((notification.getType() == NotificationType.ACCOMMODATION_RATED) && host.isAccommodationRatingNotificationEnabled()){
                        results.add(notification);
                    }
                    if((notification.getType() == NotificationType.HOST_RATED) && host.isHostRatingNotificationEnabled()){
                        results.add(notification);
                    }
                    if((notification.getType() == NotificationType.RESERVATION_CANCELED) && host.isCancellationNotificationEnabled()){
                        results.add(notification);
                    }
                    if((notification.getType() == NotificationType.RESERVATION_CREATED) && host.isReservationCreatedNotificationEnabled()){
                        results.add(notification);
                    }
                }
            }
        }

        return results;

    }
}
