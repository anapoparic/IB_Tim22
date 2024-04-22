package rs.ac.uns.ftn.asd.BookedUp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private Long id;
    private String country;
    private String city;
    private String postalCode;
    private String streetAndNumber;
    private boolean active = true;
    private double latitude;
    private double longitude;
}
