package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import org.example.domain.AngajatIMC;
import org.example.repository.interfaces.UserRepository;

import java.util.function.Consumer;

public class UserRepositoryImpl implements UserRepository {
    private EntityManagerFactory emf;

    public UserRepositoryImpl(EntityManagerFactory emf){
        this.emf = emf;
    }

    @Override
    public AngajatIMC findByUsername(String username) {
        try (EntityManager em = this.emf.createEntityManager()) {
            return em.createQuery("SELECT u FROM AngajatIMC u WHERE u.username = :username", AngajatIMC.class)
                    .setParameter("username", username).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void save(AngajatIMC entity) {
        executeInTransaction(em -> em.persist(entity));
    }

    @Override
    public void update(AngajatIMC entity) {
        executeInTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(Long id) {
        executeInTransaction(em -> {
            AngajatIMC user = em.find(AngajatIMC.class, id);
            if (user != null) {
                em.remove(user);
            }
        });
    }

    @Override
    public AngajatIMC findById(Long id) {
        try (EntityManager em = this.emf.createEntityManager()) {
            return em.find(AngajatIMC.class, id);
        }
    }

    @Override
    public Iterable<AngajatIMC> findAll() {
        try (EntityManager em = this.emf.createEntityManager()) {
            return em.createQuery("FROM AngajatIMC", AngajatIMC.class).getResultList();
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
