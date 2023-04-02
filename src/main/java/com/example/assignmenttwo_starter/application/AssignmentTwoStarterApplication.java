package com.example.assignmenttwo_starter.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import java.awt.image.BufferedImage;

@SpringBootApplication
@EntityScan("com.example.assignmenttwo_starter.model")
@ComponentScan({"com.example.assignmenttwo_starter.services", "com.example.assignmenttwo_starter.web.rest.controllers"})
@EnableJpaRepositories("com.example.assignmenttwo_starter.repositories")

public class AssignmentTwoStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssignmentTwoStarterApplication.class, args);
    }
    /**
     * Also, we need to manually register a message converter for BufferedImage
     * HTTP Responses because there is no default:
     * Required for the QrCode Image
     *
     * @link <a href="https://www.baeldung.com/java-generating-barcodes-qr-codes">Java Generating Barcodes and QR Codes</a>
     * @return A new BufferedImageHttpMessageConverter
     */
    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter()
    {
        return new BufferedImageHttpMessageConverter();
    }
}
