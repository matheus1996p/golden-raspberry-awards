package com.outsera.goldenraspberry.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "awards")
public record AwardsProperties(String csvLocation) {
}
