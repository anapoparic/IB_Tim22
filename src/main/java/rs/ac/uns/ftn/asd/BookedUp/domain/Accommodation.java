package rs.ac.uns.ftn.asd.BookedUp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.AccommodationStatus;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.AccommodationType;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.Amenity;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.PriceType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //private Host host;
    @Column(unique = false, nullable = false)
    private String name;

    @Column(unique = false, nullable = true)
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", unique = true)
    private Address address;

    @Column(unique = false, nullable = false)
    private double price;

    @Column(unique = false, nullable = true)
    private int minGuests;

    @Column(unique = false, nullable = true)
    private int maxGuests;

    @Column(unique = false, nullable = true)
    private int cancellationDeadline;

    @Column(nullable = false)
    private boolean automaticReservationAcceptance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceType priceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccommodationType type;

    @ElementCollection(targetClass = Amenity.class)
    @CollectionTable(name = "amenities", joinColumns = @JoinColumn(name = "accommodation_id"))
    @Enumerated(EnumType.STRING)
    private List<Amenity> amenities;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name = "accommodation_id", nullable = true)
    private List<Photo> photos;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private List<DateRange> availability;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private List<PriceChange> priceChanges;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "accommodation")
    private List<Reservation> reservations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "accommodation")
    private List<Review> reviews;

    @Column(unique = false, nullable = true)
    private double averageRating;

    @ManyToOne
    @JoinColumn(name = "host_id", nullable = false)
    private Host host;

    @Column(nullable = false)
    private boolean active = true;



    public void copyValues(Accommodation accommodation) {
        this.host = accommodation.getHost();
        this.name = accommodation.getName();
        this.address = accommodation.getAddress();
        this.description = accommodation.getDescription();
        this.amenities = accommodation.getAmenities();
        this.photos = accommodation.getPhotos();
        this.minGuests = accommodation.getMinGuests();
        this.maxGuests = accommodation.getMaxGuests();
        this.type = accommodation.getType();
        this.availability = accommodation.getAvailability();
        this.price = accommodation.getPrice();
        this.priceType = accommodation.getPriceType();
        this.status = accommodation.getStatus();
        this.priceChanges = accommodation.getPriceChanges();
        this.cancellationDeadline = accommodation.getCancellationDeadline();
        this.automaticReservationAcceptance = accommodation.isAutomaticReservationAcceptance();
        this.reservations = accommodation.getReservations();
        this.reviews = accommodation.getReviews();
        this.averageRating = accommodation.getAverageRating();

    }


}
