package org.example.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SeleniumWhatsApp {
    private WebDriver driver;

    public void setupDriver() {
        if (driver == null) {
            WebDriverManager.chromedriver().setup(); // Automatically setups the driver for Chrome.
            System.out.println("Creating new chrome driver.");
            driver = new ChromeDriver();
        } else {
            System.out.println("Chrome driver already exists.");
        }
    }

    public void openWhatsApp() {
        if (!Objects.equals(driver.getCurrentUrl(), "https://web.whatsapp.com/")) {
            driver.get("https://web.whatsapp.com/");
            System.out.println("Opening WhatsApp.");
        } else {
            System.out.println("WhatsApp is already open.");
        }
    }

    public void sendMessage(String contactName, String message, int maxRetries) throws RuntimeException {
        if (!isLoggedIn(maxRetries)) {
            return;
        }

        boolean hasFoundContact = false;
        for (int currentRetry = 0; currentRetry < maxRetries; currentRetry++) {
            hasFoundContact = searchContact(contactName);
            if (hasFoundContact) {
                try {
                    WebElement textBar = driver.findElement(By.xpath("//*[@id=\"main\"]/footer/div[1]/div/span[2]/div/div[2]/div[1]"));
                    textBar.sendKeys(message + Keys.ENTER);
                    break;
                } catch (NoSuchElementException e) {
                    System.out.println("The contact " + contactName + " has been found but the message couldn't be sent. " +
                                        "Trying again...");
                }
            }
            else {
                System.out.println("The contact " + contactName + " hasn't been found. Trying again...");
            }
        }

        if (!hasFoundContact) {
            throw new RuntimeException("The contact " + contactName + " hasn't been found.");
        }
    }

    private boolean searchContact(String contactName) {
        WebElement searchBar = driver.findElement(By.xpath("//*[@id=\"side\"]/div[1]/div/div/div[2]/div/div[2]"));
        searchBar.clear();
        searchBar.sendKeys(contactName + Keys.ENTER);

        try {
            Thread.sleep(500); // Waits for the contact to appear.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            WebElement contact = driver.findElement(By.xpath(("//*[@id=\"main\"]/header/div[2]/div/div/span")));
            // Contact name that appears in the header of the contact.
            return contact.getText().equals(contactName); // The contact has or not been found if the name equals the input.

        } catch (NoSuchElementException e) {
            return false; // The contact hasn't been found.
        }
    }

    public boolean isLoggedIn(int maxRetries) {
        int retryDelay = 5;

        for (int currentRetry = 0; currentRetry < maxRetries; currentRetry++) {
            try {
                driver.findElement(By.xpath("//*[@id=\"side\"]/div[1]/div/div/div[2]/div/div[2]")); // Tries to find the search bar.
                System.out.println("The user is logged in.");
                return true; // The user is logged in.
            } catch (Exception e) {
                System.out.println("The user isn't logged in. Checking again in " + retryDelay + " seconds.");
                try {
                    TimeUnit.SECONDS.sleep(retryDelay);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                // The user isn't logged in.
            }
        }
        return false;
    }

    public static void appendWhatsAppNewLine(StringBuilder stringBuilder) {
        stringBuilder.append(Keys.SHIFT);
        stringBuilder.append(Keys.ENTER);
        stringBuilder.append(Keys.SHIFT);
    }

    public void closeDriver() {
        driver.close();
    }
}

