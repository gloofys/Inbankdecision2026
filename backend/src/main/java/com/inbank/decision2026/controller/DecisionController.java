package com.inbank.decision2026.controller;

import com.inbank.decision2026.dto.DecisionRequest;
import com.inbank.decision2026.dto.DecisionResponse;
import com.inbank.decision2026.exceptions.InvalidLoanAmountException;
import com.inbank.decision2026.exceptions.InvalidLoanPeriodException;
import com.inbank.decision2026.exceptions.InvalidPersonalCodeException;
import com.inbank.decision2026.model.Decision;
import com.inbank.decision2026.service.DecisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DecisionController {

    private final DecisionService decisionService;

    public DecisionController(DecisionService decisionService) {
        this.decisionService = decisionService;
    }

    @PostMapping("/decision")
    public ResponseEntity<DecisionResponse> makeDecision(@RequestBody DecisionRequest request)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException {

        Decision decision = decisionService.calculateApprovedLoan(
                request.personalCode(),
                request.loanAmount(),
                request.loanPeriod()
        );

        DecisionResponse response = new DecisionResponse(
                decision.errorMessage() == null,
                decision.approvedAmount(),
                decision.approvedPeriod(),
                decision.errorMessage()
        );

        return ResponseEntity.ok(response);
    }
}