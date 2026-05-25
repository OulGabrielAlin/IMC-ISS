package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.example.domain.Credit;
import org.example.domain.datatypes.CreditStatus;
import org.example.repository.interfaces.CreditRepository;

import java.util.function.Consumer;

public class CreditRepositoryImpl implements CreditRepository {
    private EntityManagerFactory emf;

    public CreditRepositoryImpl(EntityManagerFactory emf){
        this.emf = emf;
    }

    @Override
    public Iterable<Credit> findActiveCreditsByClientId(Long clientId) {
        try (EntityManager em = this.emf.createEntityManager()) {
            return em.createQuery("SELECT c FROM Credit c where c.creditStatus = :status AND c.creditApplication.client.id = :id", Credit.class)
                    .setParameter("status", CreditStatus.ACTIVE)
                    .setParameter("id", clientId)
                    .getResultList();
        }
    }

    @Override
    public Iterable<Credit> findHistoryOfCreditsByClientId(Long clientId) {
        try (EntityManager em = this.emf.createEntityManager()) {
            return em.createQuery("SELECT c FROM Credit c where c.creditApplication.client.id = :id", Credit.class)
                    .setParameter("id", clientId)
                    .getResultList();
        }
    }

    @Override
    public Iterable<Credit> findAllOverdueCredits() {
        try (EntityManager em = this.emf.createEntityManager()) {
            return em.createQuery("SELECT c FROM Credit c WHERE c.creditStatus = :status", Credit.class)
                    .setParameter("status", CreditStatus.DUE)
                    .getResultList();
        }
    }

    @Override
    public void save(Credit entity) {
        executeInTransaction(em -> em.persist(entity));
    }

    @Override
    public void update(Credit entity) {
        executeInTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(Long id) {
        executeInTransaction(em -> {
            Credit entity = em.find(Credit.class, id);
            if (entity != null) {
                em.remove(entity);
            }
        });
    }

    @Override
    public Credit findById(Long id) {
        try (EntityManager em = this.emf.createEntityManager()) {
            return em.find(Credit.class, id);
        }
    }

    @Override
    public Iterable<Credit> findAll() {
        try (EntityManager em = this.emf.createEntityManager()) {
            return em.createQuery("SELECT c FROM Credit c", Credit.class).getResultList();
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
