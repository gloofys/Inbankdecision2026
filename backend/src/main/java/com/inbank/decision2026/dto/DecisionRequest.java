package com.inbank.decision2026.dto;

public record DecisionRequest(
        String personalCode,
        Integer loanAmount,
        Integer loanPeriod
) {
}
