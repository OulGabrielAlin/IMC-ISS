package org.example.domain;

import jakarta.persistence.*;
import org.example.domain.datatypes.PaymentMethod;

import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private LocalDate paymentDate;
    private Double paymentAmount;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @ManyToOne
    @JoinColumn(name = "rate_id")
    private Rate rate;

    public Payment() {
    }

    public Payment(LocalDate paymentDate, Double paymentAmount, PaymentMethod paymentMethod, Rate rate) {
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.rate = rate;
    }

    public Payment(LocalDate paymentDate, Double paymentAmount, PaymentMethod paymentMethod) {
        this.paymentDate = paymentDate;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public Double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id) && Objects.equals(paymentDate, payment.paymentDate) && Objects.equals(paymentAmount, payment.paymentAmount) && paymentMethod == payment.paymentMethod && Objects.equals(rate, payment.rate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, paymentDate, paymentAmount, paymentMethod, rate);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentDate=" + paymentDate +
                ", paymentAmount=" + paymentAmount +
                ", paymentMethod=" + paymentMethod +
                ", rate=" + rate +
                '}';
    }
}
