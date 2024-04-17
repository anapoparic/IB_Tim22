package rs.ac.uns.ftn.asd.BookedUp.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = false, nullable = false)
    private String country;

    @Column(unique = false, nullable = false)
    private String city;

    @Column(unique = false, nullable = true)
    private String postalCode;

    @Column(unique = false, nullable = false)
    private String streetAndNumber;

    @Column(unique = false, nullable = false)
    private double latitude;

    @Column(unique = false, nullable = false)
    private double longitude;

    @Column(nullable = false)
    private boolean active = true;




    // Function to copy values from another address
//    public void copyValues(Address address) {
//        this.country = address.getCountry();
//        this.city = address.getCity();
//        this.postalCode = address.getPostalCode();
//        this.streetAndNumber = address.getStreetAndNumber();
//    }
}
