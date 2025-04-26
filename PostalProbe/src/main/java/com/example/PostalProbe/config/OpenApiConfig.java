package com.example.PostalProbe.config;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Postal Probe")
                        .description("API for managing postal pincode information")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Suyash Baoney")
                                .url("https://github.com/suyash-rgb")
                                .email("suyashbaoney58@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}
