package org.example.service;

import org.example.domain.Credit;
import org.example.domain.CreditApplication;
import org.example.domain.Rate;
import org.example.domain.datatypes.CreditApplicationStatus;
import org.example.domain.datatypes.CreditStatus;
import org.example.domain.datatypes.PaymentStatus;
import org.example.domain.datatypes.Period;
import org.example.repository.interfaces.CreditApplicationRepository;
import org.example.repository.interfaces.CreditRepository;
import org.example.service.dto.EligibilityResult;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreditService {
    private CreditApplicationRepository creditApplicationRepository;
    private CreditRepository creditRepository;
    private EligibilityService eligibilityService;

    private static final Double ANNUAL_INTEREST_RATE = 0.18;

    public CreditService(CreditApplicationRepository creditApplicationRepository, CreditRepository creditRepository, EligibilityService eligibilityService) {
        this.creditApplicationRepository = creditApplicationRepository;
        this.creditRepository = creditRepository;
        this.eligibilityService = eligibilityService;
    }

    public List<Rate> simulateCredit(Double sum, Period period){
        return calculateRates(sum, period.getNumberOfMonths(), null);
    }

    public EligibilityResult registerCreditApplication(CreditApplication creditApplication){
        EligibilityResult result = eligibilityService.evaluateEligibility(creditApplication);
        creditApplication.setScore(BigDecimal.valueOf(result.getScore()));
        creditApplicationRepository.save(creditApplication);

        return result;
    }

    public Credit finalizeContract(Long creditApplicationId){
        CreditApplication application = creditApplicationRepository.findById(creditApplicationId);

        if (application == null){
            return null;
        }

        if (application.getStatus() != CreditApplicationStatus.APPROVED) {
            throw new IllegalStateException("A credit can not be generated for a rejected application.");
        }

        Credit credit = new Credit();
        credit.setCreditApplication(application);
        credit.setCreditAmount(application.getRequestedSum());
        credit.setCreditStatus(CreditStatus.ACTIVE);
        credit.setCreditPeriod(application.getPeriod());
        credit.setContractNumber(generateAlphanumeric());
        credit.setInterest(0.18);

        List<Rate> rates = calculateRates(credit.getCreditAmount(), application.getPeriod().getNumberOfMonths(), credit);
        credit.setRates(rates);

        creditRepository.save(credit);

        return credit;
    }

    private List<Rate> calculateRates(Double amount, Integer noOfMonths, Credit creditReference) {
        List<Rate> rates = new ArrayList<>();
        Double monthlyInterestRate = ANNUAL_INTEREST_RATE / 12;

        Double amountPerMonth = amount / noOfMonths;
        Double remainingBalance = amount;

        LocalDate currentDeadline = LocalDate.now().plusMonths(1);

        for (int i = 1; i <= noOfMonths; i++) {
            Rate rate = new Rate();
            Double interestForMonth = remainingBalance * monthlyInterestRate;
            Double totalRateAmount = amountPerMonth + interestForMonth;

            rate.setAmount(totalRateAmount);
            rate.setDeadline(currentDeadline);
            rate.setInterest(monthlyInterestRate);
            rate.setPaymentStatus(PaymentStatus.UNPAID);

            if (creditReference != null) {
                rate.setCredit(creditReference);
            }

            rates.add(rate);

            remainingBalance -= amountPerMonth;
            currentDeadline = currentDeadline.plusMonths(1);
        }

        return rates;
    }

    private String generateAlphanumeric() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder("CR-");

        for (int i = 0; i < 5; i++) {
            int randomIndex = random.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }

        return sb.toString();
    }

    public Iterable<CreditApplication> getAllCreditApplications() {
        return creditApplicationRepository.findAll();
    }

    public Iterable<Credit> getAllCredits() {
        return creditRepository.findAll();
    }
}
