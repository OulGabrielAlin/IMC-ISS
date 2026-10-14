package org.example.repository.interfaces;

import org.example.domain.Rate;

import java.time.LocalDate;

public interface RateRepository extends CRUDRepository<Long, Rate>{
    Rate findNextDueDate(Long creditId);
    Iterable<Rate> findAllOverdueRates(LocalDate date);
    Iterable<Rate> findAllActiveRatesByClient(String cnp);
}
