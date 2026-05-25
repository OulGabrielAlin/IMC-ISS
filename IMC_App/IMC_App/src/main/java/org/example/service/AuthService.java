package org.example.service;

import org.example.domain.Administrator;
import org.example.domain.AgentCredit;
import org.example.domain.AngajatIMC;
import org.example.domain.datatypes.AdminAccesLevel;
import org.example.repository.interfaces.UserRepository;
import org.example.service.validator.UserValidator;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Random;

public class AuthService {
    private UserRepository userRepository;
    private UserValidator userValidator;

    public AuthService(UserRepository userRepository, UserValidator userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    public AngajatIMC login(String username, String password) {
        AngajatIMC employee = userRepository.findByUsername(username);
        if (employee != null && BCrypt.checkpw(password, employee.getPassword()) ) {
            return employee;
        }

        throw new IMCException("Invalid username or password");
    }

    public void addNewUser(AngajatIMC employee) {
        if (employee != null) {
            userValidator.validate(employee);
            if (employee.getRole().equals("ADMIN")) {
                Administrator admin = new Administrator();
                admin.setUsername(employee.getUsername());
                admin.setPassword(BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt()));
                admin.setRole("ADMIN");
                admin.setFirstName(employee.getFirstName());
                admin.setAdminAccesLevel(AdminAccesLevel.SUPERUSER);
                userRepository.save(admin);
            }
            else if (employee.getRole().equals("USER")) {
                userValidator.validate(employee);
                AgentCredit agent = new AgentCredit();
                agent.setUsername(employee.getUsername());
                agent.setPassword(BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt()));
                agent.setRole("USER");
                agent.setFirstName(employee.getFirstName());
                agent.setAgentCode("AG0" + Random.from(new Random()).nextInt() % 100);
                userRepository.save(agent);
            }
        }
    }
}
