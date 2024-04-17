package rs.ac.uns.ftn.asd.BookedUp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;

    private final static String PAGE_URL = "http://localhost:4200/login";

    @FindBy(id = "emailInput")
    private WebElement emailInput;

    @FindBy(id = "passwordInput")
    private WebElement passwordInput;

    @FindBy(id = "keepMeSignedIn")
    private WebElement keepMeSignedInCheckbox;

    @FindBy(id = "forgotPasswordText")
    private WebElement forgotPasswordLink;

    @FindBy(id = "continueEmail")
    private WebElement continueWithEmailButton;

    @FindBy(id = "dontHaveAn")
    private WebElement dontHaveAnContainer;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        driver.get(PAGE_URL);
        PageFactory.initElements(driver, this);
    }


    public boolean isPageOpened() {
        WebElement titleElement = (new WebDriverWait(driver, 15))
                .until(ExpectedConditions.presenceOfElementLocated(By.id("title-login")));

        String titleText = titleElement.findElement(By.className("sign-in6")).getText();
        return "Sign in".equals(titleText.trim());
    }


    public void inputUsername(String username) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOf(emailInput));
//        emailInput.click();
        emailInput.clear();
        emailInput.sendKeys(username);
    }

    public void inputPassword(String password) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        wait.until(ExpectedConditions.visibilityOf(passwordInput));
//        passwordInput.click();
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    public void clickLoginBtn() {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        Actions actions = new Actions(driver);
        wait.until(ExpectedConditions.visibilityOf(continueWithEmailButton));
        actions.moveToElement(continueWithEmailButton).perform();
//        WebElement model = wait.until(ExpectedConditions.visibilityOf(continueWithEmailButton));
        continueWithEmailButton.click();
//        continueWithEmailButton.click();
    }

    public void clickForgotPasswordLink() {
        forgotPasswordLink.click();
    }

    public void checkKeepMeSignedIn() {
        if (!keepMeSignedInCheckbox.isSelected()) {
            keepMeSignedInCheckbox.click();
        }
    }

    public boolean isDontHaveAnContainerVisible() {
        return dontHaveAnContainer.isDisplayed();
    }
}
