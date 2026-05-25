package org.example.repository.interfaces;

import org.example.domain.Rate;

import java.time.LocalDate;

public interface RateRepository extends CRUDRepository<Long, Rate>{
    Rate findNextDueDate(Long creditId);
    Iterable<Rate> findAllOverdueCredits(LocalDate date);
}
