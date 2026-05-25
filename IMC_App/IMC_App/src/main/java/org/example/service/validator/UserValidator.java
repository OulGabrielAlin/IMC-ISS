package org.example.service.validator;

import org.example.domain.AngajatIMC;

public class UserValidator implements Validator<AngajatIMC>{
    @Override
    public void validate(AngajatIMC entity) {
        StringBuilder errors = new StringBuilder();

        if(entity == null){
            errors.append("An angajatIMC object is required.");
        }

        if (entity != null && (entity.getUsername() == null || entity.getUsername().isEmpty())){
            errors.append("Username is required.");
        }

        if (entity != null && (entity.getPassword() == null || entity.getPassword().isEmpty())){
            errors.append("Password is required.");
        }

        if (!errors.isEmpty()){
            throw new ValidationException(errors.toString());
        }
    }
}
