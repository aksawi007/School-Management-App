package org.sma.admin.core.app;

import org.sma.platform.core.annotation.SmaApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SmaApplication
@EnableJpaRepositories(basePackages = "org.sma.jpa.repository")
@EntityScan(basePackages = "org.sma.jpa.model")
@ComponentScan(basePackages = {"org.sma.admin", "org.sma.platform.core", "org.sma.security.auth"})
public class SmaAdminCoreApp {

    public static void main(String[] args) {
        SpringApplication.run(SmaAdminCoreApp.class, args);
    }
}
