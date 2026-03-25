package com.inbank.decision2026.dto;

public record DecisionResponse(
        boolean approved,
        Integer approvedAmount,
        Integer approvedPeriod,
        String errorMessage,
        DecisionReason reason
) {
}
