package org.example;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.LoginController;
import org.example.domain.Administrator;
import org.example.domain.AgentCredit;
import org.example.domain.AngajatIMC;
import org.example.repository.ClientRepositoryImpl;
import org.example.repository.CreditRepositoryImpl;
import org.example.repository.UserRepositoryImpl;
import org.example.repository.interfaces.ClientRepository;
import org.example.repository.interfaces.CreditRepository;
import org.example.repository.interfaces.UserRepository;
import org.example.repository.utils.JPAUtils;
import org.example.service.AuthService;
import org.example.service.ClientService;
import org.example.service.validator.ClientValidator;
import org.example.service.validator.UserValidator;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        EntityManagerFactory emf = JPAUtils.getEntityManagerFactory();
        UserRepository userRepository = new UserRepositoryImpl(emf);
        CreditRepository creditRepository = new CreditRepositoryImpl(emf);
        ClientRepository clientRepository = new ClientRepositoryImpl(emf);

        ClientValidator clientValidator = new ClientValidator();
        UserValidator userValidator = new UserValidator();

        AuthService authService = new AuthService(userRepository, userValidator);
        ClientService clientService = new ClientService(clientRepository, creditRepository, clientValidator);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setServices(authService, clientService);

        stage.setScene(new Scene(root));
        stage.setTitle("Login");
        stage.show();
    }
}