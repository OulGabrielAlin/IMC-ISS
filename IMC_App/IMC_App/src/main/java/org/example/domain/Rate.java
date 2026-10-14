package org.example.domain;

import jakarta.persistence.*;
import org.example.domain.datatypes.PaymentStatus;

import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Rate {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private LocalDate deadline;

    private Double amount;
    private Double interest;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private Double penalties = 0.0;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;

    public Rate() {
    }

    public Rate(LocalDate deadline, Double amount, Double interest, PaymentStatus paymentStatus, Credit credit) {
        this.deadline = deadline;
        this.amount = amount;
        this.interest = interest;
        this.paymentStatus = paymentStatus;
        this.credit = credit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public Double getPenalties() {
        return penalties;
    }

    public void setPenalties(Double penalties) {
        this.penalties = penalties;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rate rate = (Rate) o;
        return Objects.equals(id, rate.id) && Objects.equals(deadline, rate.deadline) && Objects.equals(amount, rate.amount) && Objects.equals(interest, rate.interest) && paymentStatus == rate.paymentStatus && Objects.equals(credit, rate.credit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deadline, amount, interest, paymentStatus, credit);
    }

    @Override
    public String toString() {
        return "Rate{" +
                "id=" + id +
                ", deadline=" + deadline +
                ", amount=" + amount +
                ", interest=" + interest +
                ", paymentStatus=" + paymentStatus +
                ", credit=" + credit +
                '}';
    }

    public void applyPenalties(Double percent) {
        if (percent != null && percent >= 0 && percent <= 100) {
            this.amount += this.amount * (percent / 100);
            this.paymentStatus = PaymentStatus.PENALIZED;
        }
    }
}
