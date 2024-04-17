package rs.ac.uns.ftn.asd.BookedUp.selenium;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import rs.ac.uns.ftn.asd.BookedUp.domain.Reservation;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.ReservationStatus;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;
import rs.ac.uns.ftn.asd.BookedUp.pages.*;
import rs.ac.uns.ftn.asd.BookedUp.service.AccommodationService;
import rs.ac.uns.ftn.asd.BookedUp.service.ReservationService;
import rs.ac.uns.ftn.asd.BookedUp.service.UserService;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class SearchTest extends TestBase {
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private AccommodationService accommodationService;


    @Test
    @DisplayName("#01-Test: Search And Filter Accommodations")
    @Sql("classpath:data.sql")
    @DirtiesContext
    public void testSearchAndFilterAccommodation() {

        //Index
        IndexPage indexPage = new IndexPage(driver, false);
        assertTrue(indexPage.isPageOpened());

        indexPage.inputLocationTxt("New York");
        indexPage.inputFromDate("2022-01-25");
        indexPage.inputToDate("2022-01-30");
        indexPage.inputGuest("2");

// Klik na dugme za pretragu
        indexPage.clickSearchButton();

        System.out.println("Finished test: Search And Filter Accommodation");


    }
}

