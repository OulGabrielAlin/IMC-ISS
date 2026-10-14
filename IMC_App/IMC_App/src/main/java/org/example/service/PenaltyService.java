package org.example.service;

import org.example.domain.Rate;
import org.example.domain.datatypes.PaymentStatus;
import org.example.repository.interfaces.RateRepository;

import java.time.LocalDate;

public class PenaltyService {
    private RateRepository rateRepository;

    private static final Double PENALTY_PERCENTAGE = 0.01;

    public PenaltyService(RateRepository rateRepository)
    {
        this.rateRepository = rateRepository;
    }

    public void generateDailyPenalties() {
        Iterable<Rate> overdueRates = this.rateRepository.findAllOverdueRates(LocalDate.now());

        Integer count = 0;
        for (Rate rate : overdueRates) {
            Double currentAmount = rate.getAmount();
            Double penalty = currentAmount * PENALTY_PERCENTAGE;

            Double newAmount = Math.round((currentAmount + penalty) * 100) / 100.0;
            rate.setAmount(newAmount);
            Double oldPenalty = rate.getPenalties();
            rate.setPenalties(oldPenalty + penalty);
            rate.setPaymentStatus(PaymentStatus.PENALIZED);

            rateRepository.update(rate);
            count++;
        }

        System.out.println("Daily Penalties generated: " + count);
    }
}
