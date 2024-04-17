package rs.ac.uns.ftn.asd.BookedUp.domain;

import jakarta.persistence.*;
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
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    private User from;

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    private User to;

    @Column(unique = false, nullable = true)
    private String title;

    @Column(unique = false, nullable = true)
    private String message;

    @Column(nullable = false)
    private Date timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private boolean active;

    public void copyValues(Notification notification) {
        this.id = notification.getId();
        this.from = notification.getFrom();
        this.to = notification.getTo();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.timestamp = notification.getTimestamp();
        this.type = notification.getType();
        this.active = notification.isActive();
    }
}
