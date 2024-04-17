package rs.ac.uns.ftn.asd.BookedUp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.ReservationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = false, nullable = true)
    private LocalDateTime createdTime;

    @Column(nullable = false)
    private Date startDate;

    @Column(nullable = false)
    private Date endDate;

    @Column(nullable = false)
    private double totalPrice;

    @Column(nullable = false)
    private int guestsNumber;

    @ManyToOne
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private boolean active = true;


//    public void copyValues(Reservation reservation) {
//        this.id = reservation.getId();
//        this.createdTime = reservation.getCreatedTime();
//        this.startDate = reservation.getStartDate();
//        this.endDate = reservation.getEndDate();
//        this.totalPrice = reservation.getTotalPrice();
//        this.guestsNumber = reservation.getGuestsNumber();
//        this.accommodation = reservation.getAccommodation();
//        this.guest = reservation.getGuest();
//        this.status = reservation.getStatus();
//    }

}
