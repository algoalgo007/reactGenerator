package com.bgspro.componentGenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ComponentGeneratorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComponentGeneratorApplication.class, args);
	}

}
