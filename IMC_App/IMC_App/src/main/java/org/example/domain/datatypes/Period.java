package org.example.domain.datatypes;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public class Period {
    private LocalDate startDate;
    private Integer numberOfMonths;

    public Period() {
        this.startDate = LocalDate.now();
        this.numberOfMonths = 0;
    }

    public Period(LocalDate now, Integer months) {
        this.startDate = now;
        this.numberOfMonths = months;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getNumberOfMonths() {
        return numberOfMonths;
    }

    public void setNumberOfMonths(Integer numberOfMonths) {
        this.numberOfMonths = numberOfMonths;
    }
}
