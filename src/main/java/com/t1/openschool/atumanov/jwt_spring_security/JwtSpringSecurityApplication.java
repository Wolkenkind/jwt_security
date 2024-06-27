package com.t1.openschool.atumanov.jwt_spring_security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@ComponentScan(
		basePackages = {"org.openapitools", "com.t1.openschool.atumanov.jwt_spring_security" , "org.openapitools.configuration"},
		nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class JwtSpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtSpringSecurityApplication.class, args);
	}
}
