package rs.ac.uns.ftn.asd.BookedUp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.NotificationType;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private UserDTO fromUserDTO;
    private UserDTO toUserDTO;
    private String title;
    private String message;
    private Date timestamp;
    private NotificationType type;
    private boolean active;

    public void copyValues(NotificationDTO dto) {
        this.fromUserDTO = dto.getFromUserDTO();
        this.toUserDTO = dto.getToUserDTO();
        this.title = dto.getTitle();
        this.message = dto.getMessage();
        this.timestamp = dto.getTimestamp();
        this.type = dto.getType();
        this.active = dto.isActive();
    }
}
