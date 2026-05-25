package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.example.domain.Client;
import org.example.repository.interfaces.ClientRepository;

import java.util.function.Consumer;

public class ClientRepositoryImpl implements ClientRepository {
    private EntityManagerFactory emf;

    public ClientRepositoryImpl(EntityManagerFactory emf){
        this.emf = emf;
    }

    @Override
    public Client findByCNP(String cnp) {
        try (EntityManager em = this.emf.createEntityManager()){
            return em.createQuery("SELECT cl FROM Client cl WHERE cl.cnp = :cnp", Client.class)
                    .setParameter("cnp", cnp)
                    .getSingleResult();
        }
    }

    @Override
    public void save(Client entity) {
        executeInTransaction(em -> em.persist(entity));
    }

    @Override
    public void update(Client entity) {
        executeInTransaction(em -> em.merge(entity));
    }

    @Override
    public void delete(Long id) {
        executeInTransaction(em -> {
            Client client = em.find(Client.class, id);
            if (client != null) {
                em.remove(client);
            }
        });
    }

    @Override
    public Client findById(Long id) {
        try (EntityManager em = this.emf.createEntityManager()){
            return em.find(Client.class, id);
        }
    }

    @Override
    public Iterable<Client> findAll() {
        try (EntityManager em = this.emf.createEntityManager()){
            return em.createQuery("SELECT cl FROM Client cl", Client.class).getResultList();
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
