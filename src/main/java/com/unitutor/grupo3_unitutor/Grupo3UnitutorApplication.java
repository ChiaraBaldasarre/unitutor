package com.unitutor.grupo3_unitutor;

import com.unitutor.grupo3_unitutor.controller.ApplicationController;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Grupo3UnitutorApplication {

	public static void main(String[] args) {
		SpringApplication.run(Grupo3UnitutorApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(ApplicationController applicationController) {
		return args -> {
			System.out.println("UNI-TUTOR Console Application: Hello World!");
			applicationController.run();
		};
	}
}
