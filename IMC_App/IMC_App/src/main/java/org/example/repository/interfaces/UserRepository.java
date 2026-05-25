package org.example.repository.interfaces;

import org.example.domain.AngajatIMC;

public interface UserRepository extends CRUDRepository<Long, AngajatIMC> {
    AngajatIMC findByUsername(String username);
}
