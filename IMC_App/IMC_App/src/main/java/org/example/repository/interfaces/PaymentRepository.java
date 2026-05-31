package org.example.repository.interfaces;

import org.example.domain.Payment;

import java.time.LocalDate;

public interface PaymentRepository extends CRUDRepository<Long, Payment>{
    Iterable<Payment> findAllInPeriod(LocalDate startDate, LocalDate endDate);
    Payment findLatestByCreditId(Long creditId);
}
