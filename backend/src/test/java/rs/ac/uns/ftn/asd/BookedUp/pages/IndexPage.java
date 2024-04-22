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

public class IndexPage {
    private WebDriver driver;

    private final static String PAGE_URL = "http://localhost:4200";

    @FindBy(id = "search-box")
    private WebElement searchBox;

    @FindBy(id = "profile-picture-host")
    private WebElement profilePicture;

    @FindBy(id = "hostDropdownContainer")
    private WebElement hostDropdownContainer;

    @FindBy(id = "reservationsNavItem")
    private WebElement reservationsNavItem;

    @FindBy(id = "signOut")
    private WebElement signOut;

    @FindBy(id = "locationTxt")
    private WebElement locationInput;

    @FindBy(id = "fromDate")
    private WebElement fromDateInput;

    @FindBy(id = "toDate")
    private WebElement toDateInput;

    @FindBy(id = "guestNumberTxt")
    private WebElement guestNumberInput;

    @FindBy(id = "searchButton")
    private WebElement searchButton;



    // Add more @FindBy annotations for other elements as needed

    public IndexPage(WebDriver driver, boolean flag) {
        this.driver = driver;
        if(!flag){
            driver.get(PAGE_URL);

        }
        PageFactory.initElements(driver, this);
    }

    public boolean isPageOpened() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        WebElement element = wait.until(ExpectedConditions.visibilityOf(searchBox));
        return element.isDisplayed();
    }

    public void openMenuHost() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        Actions actions = new Actions(driver);
        wait.until(ExpectedConditions.visibilityOf(profilePicture)).isDisplayed();
        actions.moveToElement(profilePicture).perform();
        profilePicture.click();

    }


    public void selectReservationsHost() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        WebElement hostDropdown = wait.until(ExpectedConditions.visibilityOf(hostDropdownContainer));
        Actions actions = new Actions(driver);
        actions.moveToElement(hostDropdown).perform();
        reservationsNavItem.click();
    }



    public void logoutHost() {
        WebDriverWait wait = new WebDriverWait(driver,15);
        WebElement hostDropdown = wait.until(ExpectedConditions.visibilityOf(hostDropdownContainer));
        Actions actions = new Actions(driver);
        actions.moveToElement(hostDropdown).perform();
        signOut.click();
    }

    public void inputLocationTxt(String location) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOf(locationInput)).clear();
        locationInput.sendKeys(location);
    }

    public void inputFromDate(String fromDate) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOf(fromDateInput)).clear();
        fromDateInput.sendKeys(fromDate);
    }

    public void inputToDate(String toDate) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOf(toDateInput)).clear();
        toDateInput.sendKeys(toDate);
    }

    public void inputGuest(String guestNumber) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOf(guestNumberInput)).clear();
        guestNumberInput.sendKeys(guestNumber);
    }

    public void clickSearchButton() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.elementToBeClickable(searchButton)).click();
    }

}
