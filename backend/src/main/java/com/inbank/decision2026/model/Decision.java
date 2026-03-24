package com.inbank.decision2026.model;

public record Decision(
        Integer loanAmount,
        Integer loanPeriod,
        String errorMessage
) {
}