package rs.ac.uns.ftn.asd.BookedUp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ReservationRequestsPage {
    private WebDriver driver;

    private final static String PAGE_URL = "http://localhost:4200/reservation-requests";

    @FindBy(id = "sort-bar")
    private WebElement sortBar;

    @FindBy(className = "waiting-reservation")
    private WebElement waitingForApprovalButton;

    @FindBy(id = "accept-button")
    private WebElement acceptButton;

    @FindBy(id = "swal2-title")
    private WebElement successPopup;

    @FindBy(css = ".swal2-confirm.swal2-styled")
    private WebElement confirmButton;

    @FindBy(id = "logo")
    private WebElement logo;

    public ReservationRequestsPage(WebDriver driver) {
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

    public void clickOnWaitingForApproval() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        Actions actions = new Actions(driver);
        wait.until(ExpectedConditions.visibilityOf(waitingForApprovalButton)).isDisplayed();
        actions.moveToElement(waitingForApprovalButton).perform();
        waitingForApprovalButton.click();
    }

    public void acceptReservation() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        Actions actions = new Actions(driver);
        wait.until(ExpectedConditions.visibilityOf(acceptButton)).isDisplayed();
        actions.moveToElement(acceptButton).perform();
        acceptButton.click();
    }

    public boolean isReservationSuccessfullyAccepted() {
        boolean isPoppupShowed = (new WebDriverWait(driver, 15))
                .until(ExpectedConditions.textToBePresentInElement(successPopup, "Reservation Approved!"));

        return isPoppupShowed;
    }

    public void clickConfirmButton() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        Actions actions = new Actions(driver);
        wait.until(ExpectedConditions.visibilityOf(successPopup)).isDisplayed();
        actions.moveToElement(confirmButton).perform();
        confirmButton.click();
    }

    public void clickLogo() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        Actions actions = new Actions(driver);
        WebElement visibleLogo = wait.until(ExpectedConditions.visibilityOf(logo));
        actions.moveToElement(logo).perform();
        visibleLogo.click();
    }
}
