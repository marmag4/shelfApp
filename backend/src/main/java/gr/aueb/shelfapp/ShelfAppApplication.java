package gr.aueb.shelfapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @EnableScheduling turns on Spring's @Scheduled support, needed by
 * ExpiryAutoWasteService's nightly sweep for products past their expiry
 * date - it's off by default, so this has to be explicit.
 */
@SpringBootApplication
@EnableScheduling
public class ShelfAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShelfAppApplication.class, args);
    }

}
