package com.outsera.goldenraspberry.web.dto;

public record ProducerAwardIntervalResponse(
        String producer,
        int interval,
        int previousWin,
        int followingWin) {
}
