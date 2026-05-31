package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.example.domain.CreditApplication;
import org.example.domain.datatypes.CreditApplicationStatus;
import org.example.repository.interfaces.CreditApplicationRepository;

import java.util.function.Consumer;

public class CreditApplicationRepositoryImpl implements CreditApplicationRepository {
    private EntityManagerFactory emf;

    public CreditApplicationRepositoryImpl (EntityManagerFactory emf){
        this.emf = emf;
    }

    @Override
    public Iterable<CreditApplication> findByStatus(CreditApplicationStatus status) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT c from CreditApplication c WHERE c.status = :status", CreditApplication.class)
                    .setParameter("status", status).getResultList();
        }
    }

    @Override
    public Iterable<CreditApplication> findByClientId(Long clientId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT c FROM CreditApplication  C where C.client.id = :id", CreditApplication.class)
                    .setParameter("id", clientId).getResultList();

        }
    }

    @Override
    public void save(CreditApplication entity) {
        executeInTransaction(em -> em.persist(entity));
    }

    @Override
    public void update(CreditApplication entity) {
        executeInTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(Long id) {
        executeInTransaction(em -> {
            CreditApplication entity = em.find(CreditApplication.class, id);
            if (entity != null) {
                em.remove(entity);
            }
        });
    }

    @Override
    public CreditApplication findById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(CreditApplication.class, id);
        }
    }

    @Override
    public Iterable<CreditApplication> findAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("select c from CreditApplication c", CreditApplication.class).getResultList();
        }
    }

    private void executeInTransaction(Consumer<EntityManager> consumer) {
        try (EntityManager em = this.emf.createEntityManager()) {
            EntityTransaction transaction = em.getTransaction();
            try {
                transaction.begin();
                consumer.accept(em);
                transaction.commit();
            }  catch (Exception ex) {
                if (transaction.isActive())
                    transaction.rollback();
                throw ex;
            }
        }
    }
}
