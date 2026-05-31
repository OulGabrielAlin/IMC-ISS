package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import org.example.domain.Payment;
import org.example.repository.interfaces.PaymentRepository;

import java.time.LocalDate;
import java.util.function.Consumer;

public class PaymentRepositoryImpl implements PaymentRepository {
    private EntityManagerFactory emf;

    public PaymentRepositoryImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Iterable<Payment> findAllInPeriod(LocalDate startDate, LocalDate endDate) {
        try(EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT p FROM Payment p WHERE p.paymentDate >= :startDate AND p.paymentDate < :endDate", Payment.class)
                    .setParameter("startDate", startDate).setParameter("endDate", endDate).getResultList();
        }
    }

    @Override
    public Payment findLatestByCreditId(Long creditId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT p FROM Payment p WHERE p.rate.credit.id = :id ORDER BY p.paymentDate DESC", Payment.class)
                    .setParameter("id", creditId)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }


    @Override
    public void save(Payment entity) {
        executeInTransaction(em -> em.persist(entity));
    }

    @Override
    public void update(Payment entity) {
        executeInTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(Long id) {
        executeInTransaction(em -> {
            Payment entity = em.find(Payment.class, id);
            if (entity != null) {
                em.remove(entity);
            }
        });
    }

    @Override
    public Payment findById(Long id) {
        try(EntityManager em = emf.createEntityManager()){
            return em.find(Payment.class, id);
        }
    }

    @Override
    public Iterable<Payment> findAll() {
        try(EntityManager em = emf.createEntityManager()){
           return em.createQuery("select p from Payment p", Payment.class).getResultList();
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
