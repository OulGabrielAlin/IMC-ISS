package org.example.domain;

import jakarta.persistence.*;
import org.example.domain.datatypes.CreditApplicationStatus;
import org.example.domain.datatypes.Period;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class CreditApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private Double requestedSum;

    @Column(precision = 10, scale = 2)
    private BigDecimal score;

    @Enumerated(EnumType.STRING)
    private CreditApplicationStatus status;

    @Embedded
    private Period period;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    public CreditApplication() {
    }

    public CreditApplication(Double requestedSum, CreditApplicationStatus status, Period period, Client client) {
        this.requestedSum = requestedSum;
        this.status = status;
        this.period = period;
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRequestedSum() {
        return requestedSum;
    }

    public void setRequestedSum(Double requestedSum) {
        this.requestedSum = requestedSum;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public CreditApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(CreditApplicationStatus status) {
        this.status = status;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreditApplication that = (CreditApplication) o;
        return Objects.equals(id, that.id) && Objects.equals(requestedSum, that.requestedSum) && Objects.equals(score, that.score) && status == that.status && Objects.equals(period, that.period) && Objects.equals(client, that.client);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, requestedSum, score, status, period, client);
    }

    @Override
    public String toString() {
        return "CreditApplication{" +
                "id=" + id +
                ", requestedSum=" + requestedSum +
                ", score=" + score +
                ", status=" + status +
                ", period=" + period +
                ", client=" + client +
                '}';
    }
}
