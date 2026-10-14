package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.domain.Administrator;
import org.example.domain.AgentCredit;
import org.example.domain.AngajatIMC;
import org.example.service.*;
import org.example.utils.MessageAlert;

import java.io.IOException;

public class LoginController {
    private AuthService authService;
    private ClientService clientService;
    private CreditService creditService;
    private PaymentService paymentService;
    private ReportService reportService;

    public void setServices(AuthService authService, ClientService clientService,  CreditService creditService, PaymentService paymentService, ReportService reportService) {
        this.authService = authService;
        this.clientService = clientService;
        this.creditService = creditService;
        this.paymentService = paymentService;
        this.reportService = reportService;
    }

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnLogin;

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        try {
            AngajatIMC employee = authService.login(username, password);
            if (employee != null) {
                String fxmlPath = "";
                String title = "";

                if (employee instanceof AgentCredit) {
                    fxmlPath = "/views/dashboard-agent.fxml";
                    title = "Agent Dashboard";
                } else if (employee instanceof Administrator) {
                    fxmlPath = "/views/dashboard-admin.fxml";
                    title = "Admin Dashboard";
                }

                loadScene(event, fxmlPath, title, employee);
            }

        } catch (IMCException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }

    }

    private void loadScene(ActionEvent event, String FxmlPath, String title, AngajatIMC angajatIMC) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FxmlPath));
            Parent root = fxmlLoader.load();

            if (angajatIMC instanceof AgentCredit) {
                DashboardAgentController controller = (DashboardAgentController) fxmlLoader.getController();
                controller.setServicesAndUser(authService, clientService, creditService, paymentService, reportService, (AgentCredit) angajatIMC);
            } else if (angajatIMC instanceof Administrator) {
                DashboardAdminController controller = (DashboardAdminController) fxmlLoader.getController();
                controller.setServicesAndUser(authService, clientService, creditService, paymentService, reportService, (Administrator) angajatIMC);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }
}
