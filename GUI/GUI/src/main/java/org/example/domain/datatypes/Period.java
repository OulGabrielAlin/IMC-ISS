package org.example.domain.datatypes;

import jakarta.persistence.Embeddable;

import java.time.LocalDate;

@Embeddable
public class Period {
    private LocalDate startDate;
    private Integer numberOfMonths;
}
