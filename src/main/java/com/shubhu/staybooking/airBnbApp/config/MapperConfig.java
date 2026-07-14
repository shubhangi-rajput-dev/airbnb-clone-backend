package com.shubhu.staybooking.airBnbApp.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class responsible for registering application-specific beans.
 */
@Configuration
public class MapperConfig {

    /**
     * Creates and registers a {@link ModelMapper} bean in the Spring
     * application context.
     *
     * <p>{@code ModelMapper} is a third-party object mapping library that
     * automatically maps data between source and destination objects having
     * similar field names. It is commonly used to convert entities to DTOs
     * and DTOs back to entities, reducing the need for manual mapping code.</p>
     *
     * <p>Since this bean is managed by the Spring container, it can be
     * injected into any Spring component using dependency injection.</p>
     *
     * @return a singleton {@link ModelMapper} instance managed by Spring
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}