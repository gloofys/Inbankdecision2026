package com.inbank.decision2026.service;

import com.inbank.decision2026.config.DecisionConstants;
import com.inbank.decision2026.exceptions.InvalidLoanAmountException;
import com.inbank.decision2026.exceptions.InvalidLoanPeriodException;
import com.inbank.decision2026.exceptions.InvalidPersonalCodeException;
import com.inbank.decision2026.model.Decision;
import org.springframework.stereotype.Service;

@Service
public class DecisionService {

    public Decision calculateApprovedLoan(String personalCode, Integer loanAmount, Integer loanPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException {

        verifyInputs(personalCode, loanAmount, loanPeriod);

        int creditModifier = getCreditModifier(personalCode);

        if (creditModifier == 0) {
            return new Decision(null, null, "Applicant has debt");
        }

        Decision bestOfferForSelectedPeriod = findBestDecisionForPeriod(creditModifier, loanPeriod);
        if (bestOfferForSelectedPeriod != null) {
            return bestOfferForSelectedPeriod;
        }

        for (int period = loanPeriod + 1; period <= DecisionConstants.MAXIMUM_LOAN_PERIOD; period++) {
            Decision bestOfferForNewPeriod = findBestDecisionForPeriod(creditModifier, period);
            if (bestOfferForNewPeriod != null) {
                return bestOfferForNewPeriod;
            }
        }

        return new Decision(null, null, "No valid loan found");
    }

    private Decision findBestDecisionForPeriod(int creditModifier, int loanPeriod) {
        int maxLoanAmountForPeriod = Math.min(
                DecisionConstants.MAXIMUM_LOAN_AMOUNT,
                creditModifier * loanPeriod
        );

        if (maxLoanAmountForPeriod < DecisionConstants.MINIMUM_LOAN_AMOUNT) {
            return null;
        }

        double creditScore = calculateCreditScore(creditModifier, maxLoanAmountForPeriod, loanPeriod);

        if (creditScore >= 1) {
            return new Decision(maxLoanAmountForPeriod, loanPeriod, null);
        }

        return null;
    }

    private double calculateCreditScore(int creditModifier, int loanAmount, int loanPeriod) {
        return ((double) creditModifier / loanAmount) * loanPeriod;
    }

    private int getCreditModifier(String personalCode) throws InvalidPersonalCodeException {
        return switch (personalCode) {
            case "49002010965" -> 0;
            case "49002010976" -> DecisionConstants.SEGMENT_1_CREDIT_MODIFIER;
            case "49002010987" -> DecisionConstants.SEGMENT_2_CREDIT_MODIFIER;
            case "49002010998" -> DecisionConstants.SEGMENT_3_CREDIT_MODIFIER;
            default -> throw new InvalidPersonalCodeException("Unsupported personal code");
        };
    }

    private void verifyInputs(String personalCode, Integer loanAmount, Integer loanPeriod)
            throws InvalidPersonalCodeException, InvalidLoanAmountException, InvalidLoanPeriodException {

        if (personalCode == null || personalCode.isBlank()) {
            throw new InvalidPersonalCodeException("Personal code is required");
        }

        if (!personalCode.matches("\\d{11}")) {
            throw new InvalidPersonalCodeException("Personal code must contain exactly 11 digits");
        }

        if (loanAmount == null
                || loanAmount < DecisionConstants.MINIMUM_LOAN_AMOUNT
                || loanAmount > DecisionConstants.MAXIMUM_LOAN_AMOUNT) {
            throw new InvalidLoanAmountException("Loan amount must be between 2000 and 10000");
        }

        if (loanPeriod == null
                || loanPeriod < DecisionConstants.MINIMUM_LOAN_PERIOD
                || loanPeriod > DecisionConstants.MAXIMUM_LOAN_PERIOD) {
            throw new InvalidLoanPeriodException("Loan period must be between 12 and 60 months");
        }
    }
}