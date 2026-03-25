package com.inbank.decision2026.model;

public record Decision(
        Integer approvedAmount,
        Integer approvedPeriod,
        String errorMessage
) {
}