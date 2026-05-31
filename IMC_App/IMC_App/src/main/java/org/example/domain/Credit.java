package org.example.domain;

import jakarta.persistence.*;
import org.example.domain.datatypes.CreditStatus;
import org.example.domain.datatypes.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String contractNumber;

    private Double creditAmount;
    private Double interest;

    @Embedded
    private Period creditPeriod;

    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;

    @OneToOne
    @JoinColumn(name = "creditapplication_id")
    private CreditApplication creditApplication;

    @OneToMany(mappedBy = "credit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rate> rates = new ArrayList<Rate>();

    public Credit() {
    }

    public Credit(String contractNumber, Double creditAmount, Double interest, Period creditPeriod, CreditStatus creditStatus, List<Rate> rates) {
        this.contractNumber = contractNumber;
        this.creditAmount = creditAmount;
        this.interest = interest;
        this.creditPeriod = creditPeriod;
        this.creditStatus = creditStatus;
        this.rates = rates;
    }

    public Credit(Double creditAmount, Double interest, Period creditPeriod, CreditStatus creditStatus, List<Rate> rates) {
        this.creditAmount = creditAmount;
        this.interest = interest;
        this.creditPeriod = creditPeriod;
        this.creditStatus = creditStatus;
        this.rates = rates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public Double getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Double creditAmount) {
        this.creditAmount = creditAmount;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public Period getCreditPeriod() {
        return creditPeriod;
    }

    public void setCreditPeriod(Period creditPeriod) {
        this.creditPeriod = creditPeriod;
    }

    public CreditStatus getCreditStatus() {
        return creditStatus;
    }

    public void setCreditStatus(CreditStatus creditStatus) {
        this.creditStatus = creditStatus;
    }

    public List<Rate> getRates() {
        return rates;
    }

    public void setRates(List<Rate> rates) {
        this.rates = rates;
    }

    public CreditApplication getCreditApplication() {
        return creditApplication;
    }

    public void setCreditApplication(CreditApplication creditApplication) {
        this.creditApplication = creditApplication;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Credit credit = (Credit) o;
        return Objects.equals(id, credit.id) && Objects.equals(contractNumber, credit.contractNumber) && Objects.equals(creditAmount, credit.creditAmount) && Objects.equals(interest, credit.interest) && Objects.equals(creditPeriod, credit.creditPeriod) && creditStatus == credit.creditStatus && Objects.equals(rates, credit.rates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contractNumber, creditAmount, interest, creditPeriod, creditStatus, rates);
    }

    @Override
    public String toString() {
        return "Credit{" +
                "id=" + id +
                ", contractNumber='" + contractNumber + '\'' +
                ", creditAmount=" + creditAmount +
                ", interest=" + interest +
                ", creditPeriod=" + creditPeriod +
                ", creditStatus=" + creditStatus +
                ", rates=" + rates +
                '}';
    }

    public double getTotalInterest() {
        if (creditAmount != null && interest != null) {
            return creditAmount * (interest / 100);
        }
        return 0.0;
    }

    public Long getClientId() {
        return creditApplication != null ? creditApplication.getClient().getId() : null;
    }
}
