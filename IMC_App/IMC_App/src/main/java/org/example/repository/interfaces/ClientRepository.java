package org.example.repository.interfaces;

import org.example.domain.Client;

public interface ClientRepository extends CRUDRepository<Long, Client>{
    Client findByCNP(String cnp);
}
