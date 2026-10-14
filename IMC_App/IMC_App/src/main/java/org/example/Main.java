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
import org.example.repository.*;
import org.example.repository.interfaces.*;
import org.example.repository.utils.JPAUtils;
import org.example.service.*;
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
        RateRepository rateRepository = new RateRepositoryImpl(emf);
        PaymentRepository paymentRepository = new PaymentRepositoryImpl(emf);
        CreditApplicationRepository creditApplicationRepository = new CreditApplicationRepositoryImpl(emf);

        ClientValidator clientValidator = new ClientValidator();
        UserValidator userValidator = new UserValidator();

        AuthService authService = new AuthService(userRepository, userValidator);
        ClientService clientService = new ClientService(clientRepository, creditRepository, clientValidator);
        PenaltyService penaltyService = new PenaltyService(rateRepository);
        EligibilityService eligibilityService = new EligibilityService();
        CreditService creditService = new CreditService(creditApplicationRepository, creditRepository, eligibilityService);
        PaymentService paymentService = new PaymentService(rateRepository, paymentRepository);
        ReportService reportService = new ReportService(creditRepository, paymentRepository, rateRepository);

        SystemStartupManager system = new SystemStartupManager(penaltyService);
        system.runDailyTasks();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        Parent root = loader.load();
        LoginController loginController = loader.getController();
        loginController.setServices(authService, clientService, creditService, paymentService, reportService);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
}