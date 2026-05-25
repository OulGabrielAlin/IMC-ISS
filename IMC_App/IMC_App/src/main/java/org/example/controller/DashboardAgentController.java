package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.domain.AgentCredit;
import org.example.service.AuthService;
import org.example.service.ClientService;
import org.example.utils.MessageAlert;

import java.io.IOException;

public class DashboardAgentController {
    private AuthService authService;
    private ClientService clientService;
    private AgentCredit user;

    public void setServicesAndUser(AuthService authService, ClientService clientService, AgentCredit user) {
        this.authService = authService;
        this.clientService = clientService;
        this.user = user;
    }

    @FXML
    public void handleClientManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/gestionare-utilizatori.fxml"));
            Parent root = loader.load();

            ClientManagementController controller = loader.getController();
            controller.setServicesAndUser(authService, clientService, user);
            Scene currentScene = ((Node) event.getSource()).getScene();
            controller.setMainScene(currentScene);

            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Client management");
            stage.show();
        } catch (IOException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();
            controller.setServices(authService, clientService);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }
}
