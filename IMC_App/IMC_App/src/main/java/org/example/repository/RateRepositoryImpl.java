package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import org.example.domain.Rate;
import org.example.domain.datatypes.PaymentStatus;
import org.example.repository.interfaces.RateRepository;

import java.time.LocalDate;
import java.util.function.Consumer;

public class RateRepositoryImpl implements RateRepository {
    private EntityManagerFactory emf;

    public RateRepositoryImpl(EntityManagerFactory emf)
    {
        this.emf = emf;
    }

    @Override
    public Rate findNextDueDate(Long creditId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT r from Rate r WHERE r.credit.id = :creditId AND r.paymentStatus != :status AND r.deadline >= CURRENT_DATE ORDER BY r.deadline ASC",  Rate.class)
                    .setParameter("creditId", creditId)
                    .setParameter("status", PaymentStatus.PAID)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Iterable<Rate> findAllOverdueRates(LocalDate date) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT r FROM Rate r WHERE r.deadline < :date AND r.paymentStatus != :status ORDER BY r.deadline DESC", Rate.class)
                    .setParameter("date", date)
                    .setParameter("status", PaymentStatus.PAID)
                    .getResultList();
        }
    }

    @Override
    public Iterable<Rate> findAllActiveRatesByClient(String cnp) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT r FROM Rate r WHERE r.credit.creditApplication.client.cnp = :cnp AND r.paymentStatus != :status order by deadline ASC", Rate.class)
                    .setParameter("cnp", cnp)
                    .setParameter("status", PaymentStatus.PAID)
                    .getResultList();
        }
    }

    @Override
    public void save(Rate entity) {
        executeInTransaction(em -> em.persist(entity));
    }

    @Override
    public void update(Rate entity) {
        executeInTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(Long id) {
        executeInTransaction(em -> {
            Rate rate = em.find(Rate.class, id);
            if (rate != null) {
                em.remove(rate);
            }
        });
    }

    @Override
    public Rate findById(Long id) {
        try(EntityManager em = emf.createEntityManager()) {
            return em.find(Rate.class, id);
        }
    }

    @Override
    public Iterable<Rate> findAll() {
        try(EntityManager em = emf.createEntityManager()) {
            return em.createQuery("select r from Rate r", Rate.class).getResultList();
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
