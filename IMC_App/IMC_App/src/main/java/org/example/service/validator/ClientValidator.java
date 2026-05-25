package org.example.service.validator;

import org.example.domain.Client;

import java.time.LocalDate;

public class ClientValidator implements Validator<Client>{
    @Override
    public void validate(Client entity) {
        StringBuilder errors = new StringBuilder();
        if (entity == null){
            errors.append("Client is required.");
            throw new ValidationException(errors.toString());
        }

        if (entity.getDateOfBirth().isBefore(LocalDate.MIN)){
            errors.append("Date of Birth is before ").append(LocalDate.MIN);
        }

        if (entity.getDateOfBirth().isAfter(LocalDate.now())){
            errors.append("Date of Birth is after ").append(LocalDate.now());
        }

        if (entity.getAdress() == null){
            errors.append("Adress is required.");
        }

        if (entity.getSalary() == null || entity.getSalary() <= 0){
            errors.append("Salary is invalid.");
        }

        if (entity.getCnp() == null || entity.getCnp().isEmpty()){
            errors.append("Cnp is required.");
        }

        if (!errors.toString().isEmpty()){
            throw new ValidationException(errors.toString());
        }
    }
}
