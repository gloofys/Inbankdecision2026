package com.inbank.decision2026.controller;

import com.inbank.decision2026.dto.DecisionReason;
import com.inbank.decision2026.dto.DecisionResponse;
import com.inbank.decision2026.exceptions.InvalidLoanAmountException;
import com.inbank.decision2026.exceptions.InvalidLoanPeriodException;
import com.inbank.decision2026.exceptions.InvalidPersonalCodeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPersonalCodeException.class)
    public ResponseEntity<DecisionResponse> handleInvalidPersonalCode(InvalidPersonalCodeException ex) {
        DecisionResponse response = new DecisionResponse(
                false,
                null,
                null,
                ex.getMessage(),
                DecisionReason.UNSUPPORTED_PERSONAL_CODE
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidLoanAmountException.class)
    public ResponseEntity<DecisionResponse> handleInvalidLoanAmount(InvalidLoanAmountException ex) {
        DecisionResponse response = new DecisionResponse(
                false,
                null,
                null,
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidLoanPeriodException.class)
    public ResponseEntity<DecisionResponse> handleInvalidLoanPeriod(InvalidLoanPeriodException ex) {
        DecisionResponse response = new DecisionResponse(
                false,
                null,
                null,
                ex.getMessage(),
                null
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}