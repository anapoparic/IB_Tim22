package rs.ac.uns.ftn.asd.BookedUp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class ReservationsPage {
    private WebDriver driver;

    private final static String PAGE_URL = "http://localhost:4200/reservation-requests";

    @FindBy(id = "sort-bar")
    private WebElement sortBar;

    @FindBy(id = "cancelledReservations")
    private WebElement cancelledReservationsButton;

    @FindBy(id = "acceptedReservations")
    private WebElement acceptedReservationsButton;

    @FindBy(id = "accept-button")
    private WebElement acceptButton;

    @FindBy(id = "swal2-title")
    private WebElement successPopup;

    @FindBy(css = ".swal2-confirm.swal2-styled")
    private WebElement confirmButton;

    @FindBy(id = "logo")
    private WebElement logo;

    public ReservationsPage(WebDriver driver) {
        this.driver = driver;
//        driver.get(PAGE_URL);
        PageFactory.initElements(driver, this);
    }

    public boolean isPageOpened() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        WebElement element = wait.until(ExpectedConditions.visibilityOf(sortBar));
        return element.isDisplayed();
    }

    public int getNumberOfReservations() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        List<WebElement> reservationElements = driver.findElements(By.cssSelector(".acc-frame"));
        return reservationElements.size();
    }

    public void clickOnCancelled() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        Actions actions = new Actions(driver);
        wait.until(ExpectedConditions.visibilityOf(cancelledReservationsButton)).isDisplayed();
        actions.moveToElement(cancelledReservationsButton).perform();
        cancelledReservationsButton.click();
    }

    public void clickOnAccepted() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        Actions actions = new Actions(driver);
        wait.until(ExpectedConditions.visibilityOf(acceptedReservationsButton)).isDisplayed();
        actions.moveToElement(acceptedReservationsButton).perform();
        acceptedReservationsButton.click();
    }

    public void clickLogo() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        Actions actions = new Actions(driver);
        WebElement visibleLogo = wait.until(ExpectedConditions.visibilityOf(logo));
        actions.moveToElement(logo).perform();
        visibleLogo.click();
    }
    public void clickViewDetailsButton(String reservationId) {
        String selector = String.format(".acc-frame[routerLink='/reservation-details/%s'] .view-details-button", reservationId);
        WebDriverWait wait = new WebDriverWait(driver, 15);
        Actions actions = new Actions(driver);
        WebElement viewDetailsButton = driver.findElement(By.cssSelector(selector));
        wait.until(ExpectedConditions.visibilityOf(viewDetailsButton)).isDisplayed();
        actions.moveToElement(viewDetailsButton).perform();
        viewDetailsButton.click();
    }




}

