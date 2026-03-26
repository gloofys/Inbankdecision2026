package com.inbank.decision2026.controller;

import com.inbank.decision2026.dto.DecisionReason;
import com.inbank.decision2026.dto.DecisionRequest;
import com.inbank.decision2026.dto.DecisionResponse;
import com.inbank.decision2026.exceptions.InvalidPersonalCodeException;
import com.inbank.decision2026.model.Decision;
import com.inbank.decision2026.service.DecisionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecisionControllerTest {

    private final DecisionService decisionService = new DecisionService();
    private final DecisionController decisionController = new DecisionController(decisionService);
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldMapDebtDecisionToApiResponse() {
        DecisionRequest request = new DecisionRequest("49002010965", 3000, 12);

        ResponseEntity<DecisionResponse> response = decisionController.makeDecision(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().approved());
        assertNull(response.getBody().approvedAmount());
        assertNull(response.getBody().approvedPeriod());
        assertEquals("Applicant has debt", response.getBody().errorMessage());
        assertEquals(DecisionReason.DEBT, response.getBody().reason());
    }

    @Test
    void shouldThrowExceptionForUnsupportedPersonalCode() {
        DecisionRequest request = new DecisionRequest("12345678901", 3000, 12);

        assertThrows(InvalidPersonalCodeException.class, () -> decisionController.makeDecision(request));
    }

    @Test
    void shouldConvertUnsupportedPersonalCodeExceptionToBadRequestResponse() {
        InvalidPersonalCodeException exception = new InvalidPersonalCodeException("Unsupported personal code");

        ResponseEntity<DecisionResponse> response = globalExceptionHandler.handleInvalidPersonalCode(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertFalse(response.getBody().approved());
        assertNull(response.getBody().approvedAmount());
        assertNull(response.getBody().approvedPeriod());
        assertEquals("Unsupported personal code", response.getBody().errorMessage());
        assertEquals(DecisionReason.UNSUPPORTED_PERSONAL_CODE, response.getBody().reason());
    }

    @Test
    void shouldMapSuccessfulDecisionToApprovedResponse() {
        DecisionRequest request = new DecisionRequest("49002010998", 5000, 12);

        ResponseEntity<DecisionResponse> response = decisionController.makeDecision(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new Decision(10000, 12, null).approvedAmount(), response.getBody().approvedAmount());
        assertEquals(12, response.getBody().approvedPeriod());
        assertEquals(true, response.getBody().approved());
        assertNull(response.getBody().errorMessage());
        assertNull(response.getBody().reason());
    }
}
