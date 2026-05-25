package org.example.repository.interfaces;

import org.example.domain.CreditApplication;
import org.example.domain.datatypes.CreditApplicationStatus;

public interface CreditApplicationRepository extends CRUDRepository<Long, CreditApplication>{
    Iterable<CreditApplication> findByStatus(CreditApplicationStatus status);
    Iterable<CreditApplication> findByClientId(Long clientId);
}
