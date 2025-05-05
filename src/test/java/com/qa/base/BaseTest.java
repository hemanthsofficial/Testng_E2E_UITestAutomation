package com.qa.base;

import com.qa.listeners.TestListener;
import com.qa.pages.*;
import com.qa.utilities.JSONDataReader;
import com.qa.utilities.Log;
import com.qa.utilities.PropertyExtractor;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;

public class BaseTest {
    WebDriver bot;
    public CustomerLoginPages customerLoginPages;
    public CustomerAddToCartPages customerAddToCartPages;
    public CustomerCartViewPages customerCartViewPages;
    public CustomerPlaceOrderPages customerPlaceOrderPages;
    public CustomerRegisterPages customerRegisterPages;
    JSONDataReader jsonDataReader;
    PropertyExtractor propertyExtractor;
    TestListener testListener;

    @BeforeTest(alwaysRun = true)
    public void setup() throws IOException {
        Log.info("Initializing browser: " + getPropertyExtractor("browser"));
        if (getPropertyExtractor("browser").contentEquals("chrome")) {
            WebDriverManager.chromedriver().setup();
            bot = new ChromeDriver();
        }
        else if (getPropertyExtractor("browser").contentEquals("edge")) {
            WebDriverManager.edgedriver().setup();
            bot = new EdgeDriver();
        }
        else if (getPropertyExtractor("browser").contentEquals("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            bot = new FirefoxDriver();
        }
        else {
            Log.error("Invalid browser specified in properties file!");
            throw new IllegalArgumentException("Invalid browser specified!");
        }
        Log.info("Navigating to: " + getPropertyExtractor("url"));
        bot.navigate().to(getPropertyExtractor("url"));
        bot.manage().window().maximize();
        bot.manage().timeouts().implicitlyWait(Duration.ofSeconds(8));
        customerLoginPages = new CustomerLoginPages(bot);
        customerAddToCartPages = new CustomerAddToCartPages(bot);
        customerCartViewPages = new CustomerCartViewPages(bot);
        customerPlaceOrderPages = new CustomerPlaceOrderPages(bot);
        customerRegisterPages = new CustomerRegisterPages(bot);
        jsonDataReader = new JSONDataReader();
        propertyExtractor = new PropertyExtractor();
        testListener = new TestListener(bot);
        Log.info("Test setup completed.");
    }

    public List<HashMap<String, String>> getJsonDataToMap(String jsonFilePath) throws IOException {
        return jsonDataReader.getJsonDataToMap(jsonFilePath);
    }

    public String getPropertyExtractor(String key) throws IOException {
        return PropertyExtractor.extractor(key);
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() {
        if (bot != null) {
            Log.info("Closing browser.");
            bot.quit();
            bot = null;
        }
    }
}