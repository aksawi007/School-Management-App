package org.sma.clep.app.app;


import org.sma.platform.core.annotation.SmaApplication;
import org.springframework.boot.SpringApplication;

@SmaApplication
public class ClepApp {

    public static void main(String[] args) {
        SpringApplication.run(ClepApp.class, args);
    }

}
