package com.outsera.goldenraspberry.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AwardIntervalResponse(
        @JsonProperty("min") List<ProducerAwardIntervalResponse> min,
        @JsonProperty("max") List<ProducerAwardIntervalResponse> max) {
}
