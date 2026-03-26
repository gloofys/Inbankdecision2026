package com.inbank.decision2026.service;

import com.inbank.decision2026.exceptions.InvalidLoanAmountException;
import com.inbank.decision2026.model.Decision;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecisionServiceTest {

    private final DecisionService decisionService = new DecisionService();

    @Test
    void shouldReturnDebtDecisionWhenApplicantHasDebt() {
        Decision decision = decisionService.calculateApprovedLoan("49002010965", 3000, 12);

        assertNull(decision.approvedAmount());
        assertNull(decision.approvedPeriod());
        assertEquals("Applicant has debt", decision.errorMessage());
    }

    @Test
    void shouldReturnApprovedLoanForStrongApplicant() {
        Decision decision = decisionService.calculateApprovedLoan("49002010998", 5000, 12);

        assertEquals(10000, decision.approvedAmount());
        assertEquals(12, decision.approvedPeriod());
        assertNull(decision.errorMessage());
    }

    @Test
    void shouldOfferLongerPeriodWhenStartingPeriodIsTooShort() {
        Decision decision = decisionService.calculateApprovedLoan("49002010976", 2000, 12);

        assertNotNull(decision);
        assertEquals(2000, decision.approvedAmount());
        assertEquals(20, decision.approvedPeriod());
        assertNull(decision.errorMessage());
    }

    @Test
    void shouldThrowExceptionWhenLoanAmountIsTooSmall() {
        assertThrows(
                InvalidLoanAmountException.class,
                () -> decisionService.calculateApprovedLoan("49002010998", 1000, 12)
        );
    }
}
