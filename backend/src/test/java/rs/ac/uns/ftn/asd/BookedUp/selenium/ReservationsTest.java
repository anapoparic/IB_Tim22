package rs.ac.uns.ftn.asd.BookedUp.selenium;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import rs.ac.uns.ftn.asd.BookedUp.domain.Reservation;
import rs.ac.uns.ftn.asd.BookedUp.domain.User;
import rs.ac.uns.ftn.asd.BookedUp.domain.enums.ReservationStatus;
import rs.ac.uns.ftn.asd.BookedUp.dto.ReservationDTO;
import rs.ac.uns.ftn.asd.BookedUp.dto.UserDTO;
import rs.ac.uns.ftn.asd.BookedUp.pages.IndexPage;
import rs.ac.uns.ftn.asd.BookedUp.pages.LoginPage;
import rs.ac.uns.ftn.asd.BookedUp.pages.ReservationRequestsPage;
import rs.ac.uns.ftn.asd.BookedUp.pages.ReservationsPage;
import rs.ac.uns.ftn.asd.BookedUp.service.AccommodationService;
import rs.ac.uns.ftn.asd.BookedUp.service.ReservationService;
import rs.ac.uns.ftn.asd.BookedUp.service.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@SpringBootTest
@TestPropertySource("classpath:application.properties")
public class ReservationsTest extends TestBase {
    @Autowired
    private UserService userService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private AccommodationService accommodationService;


    @Test
    @DisplayName("#01-Test: Host Approve Reservation")
    @Sql("classpath:data.sql")
    @DirtiesContext
    public void testViewAndApproveReservationHost() {
        //Login
        LoginPage loginPage = new LoginPage(driver);
        assertTrue(loginPage.isPageOpened());
        loginPage.inputUsername("ana.anic@example.com");
        loginPage.inputPassword("anapass");
        loginPage.clickLoginBtn();

        //Index
        IndexPage indexPage = new IndexPage(driver,true);
        assertTrue(indexPage.isPageOpened());
        indexPage.openMenuHost();
        indexPage.selectReservationsHost();

        //ReservationRequests
        ReservationRequestsPage reservationRequestsPage = new ReservationRequestsPage(driver);
        assertTrue(reservationRequestsPage.isPageOpened());

        int initialReservationCountOnPage = reservationRequestsPage.getNumberOfReservations();
        assertEquals(initialReservationCountOnPage, 12, "Number of reservations on the ReservationRequestsPage is not as expected");

        reservationRequestsPage.clickOnWaitingForApproval();

        initialReservationCountOnPage = reservationRequestsPage.getNumberOfReservations();
        assertEquals(initialReservationCountOnPage, 4, "Number of reservations on the ReservationRequestsPage is not as expected");

        reservationRequestsPage.acceptReservation();
        assertTrue(reservationRequestsPage.isReservationSuccessfullyAccepted());
        reservationRequestsPage.clickConfirmButton();

//        List<Reservation> overlappingReservations = reservationService.getOverlappingReservations(hostId, ReservationStatus.WAITING_FOR_APPROVAL, acceptedReservation);
//        assertEquals(overlappingReservations.size(), 2, "Number of overlapping reservations is not as expected");
//
//        for (Reservation overlappingReservation : overlappingReservations) {
//            assertEquals(overlappingReservation.getStatus(), ReservationStatus.REJECTED, "Overlapping reservation was not automatically rejected");
//        }

        reservationRequestsPage.clickOnWaitingForApproval();

        initialReservationCountOnPage = reservationRequestsPage.getNumberOfReservations();
        assertEquals(initialReservationCountOnPage, 3, "Number of reservations on the ReservationRequestsPage is not as expected");

        reservationRequestsPage.clickLogo();

        //Index
        IndexPage indexPage1 = new IndexPage(driver,true);
        assertTrue(indexPage1.isPageOpened());
        indexPage1.openMenuHost();
        indexPage1.logoutHost();

        System.out.println("Finished test: Host Approve Reservation");


    }

    @Test
    @DisplayName("#02-Test: View and Cancel Reservation - Guest")
    @Sql("classpath:data.sql")
    @DirtiesContext
    public void testViewAndCancelReservationGuest() {
        //Login
        LoginPage loginPage = new LoginPage(driver);
        assertTrue(loginPage.isPageOpened());
        loginPage.inputUsername("mila.milicevic@example.com");
        loginPage.inputPassword("milinpass");
        loginPage.clickLoginBtn();

        //Index
        IndexPage indexPage = new IndexPage(driver,true);
        assertTrue(indexPage.isPageOpened());
        indexPage.openMenuHost();
        indexPage.selectReservationsHost();

        //Reservations
        ReservationsPage reservationsPage = new ReservationsPage(driver);
        assertTrue(reservationsPage.isPageOpened());

        int initialReservationCountOnPage = reservationsPage.getNumberOfReservations();
        assertEquals(initialReservationCountOnPage, 5, "Number of reservations on the ReservationRequestsPage is not as expected");

        reservationsPage.clickOnCancelled();
        initialReservationCountOnPage = reservationsPage.getNumberOfReservations();
        assertEquals(initialReservationCountOnPage, 0, "Number of reservations on the ReservationRequestsPage is not as expected");

        reservationsPage.clickOnAccepted();
        initialReservationCountOnPage = reservationsPage.getNumberOfReservations();
        assertEquals(initialReservationCountOnPage, 3, "Number of reservations on the ReservationRequestsPage is not as expected");

//        reservationsPage.clickViewDetailsButton(String.valueOf(4L));

//        List<Reservation> overlappingReservations = reservationService.getOverlappingReservations(hostId, ReservationStatus.WAITING_FOR_APPROVAL, acceptedReservation);
//        assertEquals(overlappingReservations.size(), 2, "Number of overlapping reservations is not as expected");
//
//        for (Reservation overlappingReservation : overlappingReservations) {
//            assertEquals(overlappingReservation.getStatus(), ReservationStatus.REJECTED, "Overlapping reservation was not automatically rejected");
//        }

//        reservationRequestsPage.clickOnWaitingForApproval();
//
//        initialReservationCountOnPage = reservationRequestsPage.getNumberOfReservations();
//        assertEquals(initialReservationCountOnPage, 3, "Number of reservations on the ReservationRequestsPage is not as expected");

        reservationsPage.clickLogo();

        //Index
        IndexPage indexPage1 = new IndexPage(driver,true);
        assertTrue(indexPage1.isPageOpened());
        indexPage1.openMenuHost();
        indexPage1.logoutHost();

        System.out.println("Finished test: Host Approve Reservation");


    }



    private int getNumberOfCanceledReservationsFromDatabase(UserDTO user) {
        int numberOfCanceledReservations = 0;
        for (Reservation reservation : reservationService.getReservationsByGuestId(user.getId())) {
            if (reservation.getStatus().equals(ReservationStatus.CANCELLED)) {
                numberOfCanceledReservations++;
            }
        }
        System.out.println(numberOfCanceledReservations);
        return numberOfCanceledReservations;
    }
}
