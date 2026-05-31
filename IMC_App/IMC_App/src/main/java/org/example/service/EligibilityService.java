package org.example.service;

import org.example.domain.Client;
import org.example.domain.CreditApplication;
import org.example.domain.datatypes.CreditApplicationStatus;
import org.example.service.dto.EligibilityResult;

public class EligibilityService {
    private static final Double MAX_DEBT_TO_INCOME_RATIO = 0.4;
    private static final Double ANNUAL_INTEREST_RATE = 0.12;

    public EligibilityResult evaluateEligibility(CreditApplication creditApplication) {
        Client client = creditApplication.getClient();

        if (client.getSalary() == null || client.getSalary() <= 0) {
            throw new IllegalArgumentException("Processing error: the client has no income registered in their profile.");
        }

        Double requestedAmount = creditApplication.getRequestedSum();
        Integer months = creditApplication.getPeriod().getNumberOfMonths();

        Double estimatedFirstRate = calculateEstimatedMaximumRate(requestedAmount, months);

        Double debtToIncomeRatio = estimatedFirstRate / client.getSalary();

        Double score = 100 - Math.round((debtToIncomeRatio * 100) * 100) / 100.0;

        if (debtToIncomeRatio <= MAX_DEBT_TO_INCOME_RATIO) {
            creditApplication.setStatus(CreditApplicationStatus.APPROVED);
            return new EligibilityResult(
                    true,
                    score,
                    "Approved. The application has obtained a favorable score."
            );
        } else {
            creditApplication.setStatus(CreditApplicationStatus.REJECTED);
            return new EligibilityResult(
                    false,
                    score,
                    "Rejected. The application has been rejected because the score: (" + score + ") is greater than the limit of 40%"
            );
        }
    }

    private Double calculateEstimatedMaximumRate(Double amount, Integer noOfMonths) {
        Double monthlyInterestRate = ANNUAL_INTEREST_RATE / 12;
        Double amountPerMonth = amount / noOfMonths;

        Double maxInterestForMonth = amount * monthlyInterestRate;

        return amountPerMonth + maxInterestForMonth;
    }
}
