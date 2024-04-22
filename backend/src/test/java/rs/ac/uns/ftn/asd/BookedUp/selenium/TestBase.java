package rs.ac.uns.ftn.asd.BookedUp.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestBase {
    public static WebDriver driver;

    @BeforeAll
    public void initializeWebDriver() {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        driver = new ChromeDriver();

        driver.manage().window().maximize();

        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    @AfterAll
    public void quitDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
}
