package org.rentin;


import org.rentin.platform.core.annotation.RentInApplication;
import org.springframework.boot.SpringApplication;

//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@RentInApplication
public class CustMngtApp {

    public static void main(String[] args) {
        SpringApplication.run(CustMngtApp.class, args);
    }

}
