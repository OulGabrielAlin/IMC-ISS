package org.example.repository.interfaces;

public interface CRUDRepository<ID, E> {
    void save(E entity);
    void update(E entity);
    void delete(ID id);
    E findById(ID id);
    Iterable<E> findAll();
}
