package com.outsera.goldenraspberry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GoldenRaspberryAwardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoldenRaspberryAwardsApplication.class, args);
    }
}
