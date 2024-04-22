package rs.ac.uns.ftn.asd.BookedUp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.asd.BookedUp.domain.Notification;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.dto.*;
import rs.ac.uns.ftn.asd.BookedUp.mapper.NotificationMapper;
import rs.ac.uns.ftn.asd.BookedUp.mapper.UserMapper;
import rs.ac.uns.ftn.asd.BookedUp.service.NotificationService;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    /*url: /api/notifications GET*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<NotificationDTO>> getNotifications() {
        Collection<Notification> notifications = notificationService.getAll();
        Collection<NotificationDTO> notificationsDTO = notifications.stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(notificationsDTO, HttpStatus.OK);
    }

    /* url: /api/notifications/1 GET*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable("id") Long id) {
        Notification notification = notificationService.getById(id);

        if (notification == null) {
            return new ResponseEntity<NotificationDTO>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<NotificationDTO>(NotificationMapper.toDto(notification), HttpStatus.OK);
    }

    /* url: /api/notifications/user/1 GET*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @GetMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<NotificationDTO>> getNotificationsByUserId(@PathVariable("id") Long id) {
        Collection<Notification> notifications = notificationService.getByUserId(id);

        Collection<NotificationDTO> notificationsDTO = notifications.stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(notificationsDTO, HttpStatus.OK);
    }

    /* url: /api/notifications/user/enabled/1 GET*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @GetMapping(value = "/user/enabled/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<NotificationDTO>> getEnabledNotificationsByUserId(@PathVariable("id") Long id) {
        Collection<Notification> notifications = notificationService.getEnabledByUserId(id);

        Collection<NotificationDTO> notificationsDTO = notifications.stream()
                .map(NotificationMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(notificationsDTO, HttpStatus.OK);
    }

    /*url: /api/notifications POST*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDTO) throws Exception {
        Notification createdNotification = null;

        try {
            createdNotification = notificationService.create(NotificationMapper.toEntity(notificationDTO));
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new NotificationDTO(),HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(NotificationMapper.toDto(createdNotification), HttpStatus.CREATED);
    }


    /* url: /api/notifications/1 PUT*/
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GUEST', 'ROLE_HOST')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationDTO> updateNotification(@Valid @RequestBody NotificationDTO notificationDTO, @PathVariable Long id)
            throws Exception {
        Notification notificationForUpdate = notificationService.getById(id);

        if (notificationForUpdate == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        notificationForUpdate.setFrom(UserMapper.toEntity(notificationDTO.getFromUserDTO()));
        notificationForUpdate.setTo(UserMapper.toEntity(notificationDTO.getToUserDTO()));
        notificationForUpdate.setTitle(notificationDTO.getTitle());
        notificationForUpdate.setMessage(notificationDTO.getMessage());
        notificationForUpdate.setTimestamp(notificationDTO.getTimestamp());
        notificationForUpdate.setType(notificationDTO.getType());
        notificationForUpdate.setActive(notificationDTO.isActive());

        notificationForUpdate = notificationService.save(notificationForUpdate);

        return new ResponseEntity<NotificationDTO>(NotificationMapper.toDto(notificationForUpdate), HttpStatus.OK);
    }

    /** url: /api/notifications/1 DELETE*/
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Notification> deleteNotification(@PathVariable("id") Long id) {
        try {
            notificationService.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Notification>(HttpStatus.NO_CONTENT);
    }
}
