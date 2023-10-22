package ru.simbirgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
public class SimbirgoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimbirgoApplication.class, args);
	}

}
