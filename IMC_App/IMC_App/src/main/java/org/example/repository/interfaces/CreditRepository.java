package org.example.repository.interfaces;

import org.example.domain.Credit;

public interface CreditRepository extends CRUDRepository<Long, Credit>{
    Iterable<Credit> findActiveCreditsByClientId(Long clientId);
    Iterable<Credit> findHistoryOfCreditsByClientId(Long clientId);
    Iterable<Credit> findAllOverdueCredits();
}
