package com.shubhu.staybooking.airBnbApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 * Main entry point of the Spring Boot application.
 *
 * @SpringBootApplication combines:
 * 1. @Configuration - Marks this class as a source of Spring Bean definitions.
 * 2. @EnableAutoConfiguration - Automatically configures Spring Boot based on the
 *    dependencies available on the classpath.
 * 3. @ComponentScan - Scans the current package and its sub-packages for Spring
 *    components such as @Controller, @Service, @Repository, and @Component.
 */
@SpringBootApplication
/*
 * Enables Spring's scheduling support.
 * If your project contains methods annotated with
 * @Scheduled and you want them to execute automatically.
 *
 * Example (PricingUpdateService):
 * @Scheduled(cron = "0 0 * * * *")
 * public void updatePrices() {
 *     // Executes every hour
 * }
 */
//@EnableScheduling
public class AirBnbAppApplication {
    /*
     * Main method where the application execution begins.
     *
     * SpringApplication.run() performs the following:
     * 1. Creates the Spring IoC Container (ApplicationContext).
     * 2. Performs component scanning.
     * 3. Creates and injects all Spring Beans.
     * 4. Applies auto-configuration.
     * 5. Starts the embedded Tomcat web server.
     * 6. Makes the application ready to receive HTTP requests.
     */
    public static void main(String[] args) {
        SpringApplication.run(AirBnbAppApplication.class, args);
    }
}
