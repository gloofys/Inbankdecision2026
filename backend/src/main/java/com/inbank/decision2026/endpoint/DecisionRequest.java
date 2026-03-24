package com.inbank.decision2026.endpoint;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DecisionRequest {
    private String personalCode;
    private Long loanAmount;
    private int loanPeriod;
}
