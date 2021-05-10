package com.productmanagement.ingress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.productmanagement.ingress")
@SpringBootApplication
public class IngressApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(IngressApiApplication.class, args);
	}

}
