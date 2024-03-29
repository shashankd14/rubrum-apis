package com.steel.product.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.License;

@SpringBootApplication
@EnableEurekaClient
@EnableCaching
@EnableAutoConfiguration
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "Rubrum APIs", version = "1.0", description = "API v1.0"))
public class RubrumServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RubrumServiceApplication.class, args);
        System.out.println("RubrumApplication Started");
	}
	
	@Bean
	public OpenAPI customOpenAPI(@Value("1.2") String appVersion) {
		return new OpenAPI().components(new Components())
				.info(new io.swagger.v3.oas.models.info.Info().title("Rubrum API").version(appVersion)
						.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}
}




