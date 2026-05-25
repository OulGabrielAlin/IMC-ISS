package org.example.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.domain.Client;
import org.example.domain.Credit;
import org.example.repository.interfaces.ClientRepository;
import org.example.repository.interfaces.CreditRepository;
import org.example.service.validator.ClientValidator;
import org.example.service.validator.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

public class ClientService {
    private ClientRepository clientRepository;
    private CreditRepository creditRepository;
    private ClientValidator clientValidator;

    public ClientService(ClientRepository clientRepository, CreditRepository creditRepository, ClientValidator clientValidator) {
        this.clientRepository = clientRepository;
        this.creditRepository = creditRepository;
        this.clientValidator = clientValidator;
    }

    public void registerClient(Client client) {
        try {
            clientValidator.validate(client);
            clientRepository.save(client);
        } catch (ValidationException e) {
            throw new IMCException(e.getMessage());
        }
    }

    public void updateClientProfile(Client client) {
        try {
            clientValidator.validate(client);
            clientRepository.update(client);
        } catch (ValidationException e) {
            throw new IMCException(e.getMessage());
        }
    }

    public void removeClient(Long clientId) {
        try {
            clientRepository.delete(clientId);
        } catch (Exception e) {
            throw new IMCException(e.getMessage());
        }
    }

    public void findClientByCNP(String cnp) {
        try {
            Client client = clientRepository.findByCNP(cnp);
        } catch (EntityNotFoundException e) {
            throw new IMCException(e.getMessage());
        }
    }

    public List<Client> findAllClients() {
        List<Client> clients = new ArrayList<>();
        clientRepository.findAll().forEach(clients::add);
        return clients;
    }

    public List<Credit> getCreditHistory(Long clientId) {
        List<Credit> credits = new ArrayList<>();
        creditRepository.findHistoryOfCreditsByClientId(clientId).forEach(credits::add);
        return credits;
    }
}
