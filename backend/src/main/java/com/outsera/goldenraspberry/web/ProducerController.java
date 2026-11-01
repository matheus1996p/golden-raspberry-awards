package com.outsera.goldenraspberry.web;

import com.outsera.goldenraspberry.service.AwardIntervalService;
import com.outsera.goldenraspberry.web.dto.AwardIntervalResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/producers")
public class ProducerController {

    private final AwardIntervalService awardIntervalService;

    public ProducerController(AwardIntervalService awardIntervalService) {
        this.awardIntervalService = awardIntervalService;
    }

    @GetMapping("/award-intervals")
    public AwardIntervalResponse awardIntervals() {
        return awardIntervalService.calculate();
    }
}
