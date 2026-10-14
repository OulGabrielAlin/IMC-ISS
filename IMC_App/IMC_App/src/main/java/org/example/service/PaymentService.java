package org.example.service;

import org.example.domain.Payment;
import org.example.domain.Rate;
import org.example.domain.datatypes.PaymentMethod;
import org.example.domain.datatypes.PaymentStatus;
import org.example.repository.interfaces.PaymentRepository;
import org.example.repository.interfaces.RateRepository;

import java.time.LocalDate;
import java.util.List;

public class PaymentService {
    private RateRepository rateRepository;
    private PaymentRepository paymentRepository;

    public PaymentService(RateRepository rateRepository, PaymentRepository paymentRepository) {
        this.rateRepository = rateRepository;
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(Long rateId, Double amountPaid, PaymentMethod paymentMethod)
    {
        Rate rate = rateRepository.findById(rateId);

        if (rate == null) {
            throw new IllegalArgumentException("Rate not found.");
        }

        if (rate.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalArgumentException("This rate has already been fully paid");
        }

        if (amountPaid <= 0) {
            throw new IllegalArgumentException("Amount paid must be greater than 0.");
        }

        Payment payment = new Payment();
        payment.setPaymentAmount(amountPaid);
        payment.setPaymentDate(LocalDate.now());
        payment.setRate(rate);
        payment.setPaymentMethod(paymentMethod);

        if (amountPaid >= rate.getAmount()) {
            rate.setPaymentStatus(PaymentStatus.PAID);
            rate.setAmount(0.0);
        } else {
            rate.setAmount(rate.getAmount() - amountPaid);
            rate.setPaymentStatus(PaymentStatus.PARTIALLY_PAID);
        }

        paymentRepository.save(payment);
        rateRepository.update(rate);

        return payment;
    }

    public Iterable<Rate> getUnpaidRates(String clientCNP) {
        return rateRepository.findAllActiveRatesByClient(clientCNP);
    }

    public LocalDate getLastPaymentDateForCredit(Long id) {
        Payment payment = paymentRepository.findLatestByCreditId(id);
        if (payment == null) {
            return null;
        }
        return payment.getPaymentDate();
    }
}
