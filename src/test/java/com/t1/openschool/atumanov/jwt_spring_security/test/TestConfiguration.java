package com.t1.openschool.atumanov.jwt_spring_security.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@SpringBootApplication(
        exclude = {DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class})
@ComponentScan(
        basePackages = {"com.t1.openschool.atumanov.jwt_spring_security.config",
                "org.openapitools",
                "com.t1.openschool.atumanov.jwt_spring_security.api.nonsecured",
                "com.t1.openschool.atumanov.jwt_spring_security.api.secured",
                "com.t1.openschool.atumanov.jwt_spring_security.config",
                "com.t1.openschool.atumanov.jwt_spring_security.filter",
                "com.t1.openschool.atumanov.jwt_spring_security.handler",
                "com.t1.openschool.atumanov.jwt_spring_security.model",
                "com.t1.openschool.atumanov.jwt_spring_security.service",
                "com.t1.openschool.atumanov.jwt_spring_security.controller",
                "org.openapitools.configuration"},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class TestConfiguration {

}
