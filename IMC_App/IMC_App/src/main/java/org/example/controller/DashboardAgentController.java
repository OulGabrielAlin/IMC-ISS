package org.example.controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.domain.*;
import org.example.service.*;
import org.example.service.dto.CreditSummaryDTO;
import org.example.utils.MessageAlert;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardAgentController {
    private AuthService authService;
    private ClientService clientService;
    private CreditService creditService;
    private PaymentService paymentService;
    private ReportService reportService;
    private AgentCredit user;

    @FXML
    private Button btnClientManagement;

    @FXML
    private Button btnSimulateCredit;

    @FXML
    private Button btnCreateCreditApplication;

    @FXML
    private Button btnPaymentProcessing;

    @FXML
    private Button btnLogout;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<CreditSummaryDTO> tableCredits;

    @FXML
    private TableColumn<CreditSummaryDTO, String> colClient;

    @FXML
    private TableColumn<CreditSummaryDTO, String> colContractNo;

    @FXML
    private TableColumn<CreditSummaryDTO, Double> colSum;

    @FXML
    private TableColumn<CreditSummaryDTO, String> colStatus;

    @FXML
    private TableColumn<CreditSummaryDTO, String> colLastPayment;

    private ObservableList<CreditSummaryDTO> model = FXCollections.observableArrayList();

    public void setServicesAndUser(AuthService authService, ClientService clientService, CreditService creditService, PaymentService paymentService, ReportService reportService, AgentCredit user) {
        this.authService = authService;
        this.clientService = clientService;
        this.creditService = creditService;
        this.paymentService = paymentService;
        this.reportService = reportService;
        this.user = user;

        loadData();
        configureSearch();
    }

    @FXML
    public void initialize() {
        tableConfigure();
    }

    private void tableConfigure() {
        colClient.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getClientName()));
        colContractNo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContractNo()));
        colSum.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSum()).asObject());
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));
        colLastPayment.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastPaymentDate()));
    }

    private void loadData() {
        model.clear();

        try {
            List<Long> realCredits = new ArrayList<>();

            Iterable<Credit> credits = creditService.getAllCredits();

            if (credits != null) {
                for (Credit credit : credits) {
                    Client client = credit.getCreditApplication().getClient();
                    String clientName = client.getLastName() + " " + client.getFirstName();
                    String cnp = client.getCnp();

                    String contractNo = credit.getContractNumber();
                    Double sum = credit.getCreditAmount();
                    String status = credit.getCreditStatus() != null ? credit.getCreditStatus().name() : "ACTIV";

                    LocalDate lastPayment = paymentService.getLastPaymentDateForCredit(credit.getId());
                    String lastPaymentDate = "-";

                    if (lastPayment != null) {
                        lastPaymentDate = lastPayment.toString();
                    }

                    model.add(new CreditSummaryDTO(clientName, cnp, contractNo, sum, status, lastPaymentDate));

                    realCredits.add(credit.getCreditApplication().getId());
                }
            }

            Iterable<CreditApplication> cereri = creditService.getAllCreditApplications();

            if (cereri != null) {
                for (CreditApplication app : cereri) {
                    if (realCredits.contains(app.getId())) {
                        continue;
                    }

                    Client client = app.getClient();
                    String clientName = client.getLastName() + " " + client.getFirstName();
                    String cnp = client.getCnp();

                    String contractNo = "CER-" + app.getId();
                    Double sum = app.getRequestedSum();
                    String status = app.getStatus() != null ? app.getStatus().name() : "PENDING";

                    model.add(new CreditSummaryDTO(clientName, cnp, contractNo, sum, status, "-"));
                }
            }

        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "Eroare la încărcarea datelor din baza de date: " + e.getMessage());
        }

    }

    private void configureSearch() {
        FilteredList<CreditSummaryDTO> filteredData = new FilteredList<>(model, p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(credit -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (credit.getClientName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                else return credit.getCnp().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<CreditSummaryDTO> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableCredits.comparatorProperty());

        tableCredits.setItems(sortedData);
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
    public void handleSimulate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/simulare.fxml"));
            Parent root = loader.load();

            SimulationController controller = loader.getController();
            controller.setServices(creditService);
            Scene currentScene = ((Node) event.getSource()).getScene();
            controller.setMainScene(currentScene);

            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Credit simulation");
            stage.show();
        } catch (IOException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    @FXML
    public void handleCreateCreditApplicationRequest(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/creare-credit.fxml"));
            Parent root = loader.load();

            CreditCreationController controller = loader.getController();
            controller.setServices(clientService, creditService);
            Scene currentScene = ((Node) event.getSource()).getScene();
            controller.setMainScene(currentScene);

            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Credit creation");
            stage.show();
        } catch (IOException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }

    @FXML
    public void handlePaymentProcessing(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/procesare-plata.fxml"));
            Parent root = loader.load();

            PaymentController controller = loader.getController();
            controller.setServices(paymentService);
            Scene currentScene = ((Node) event.getSource()).getScene();
            controller.setMainScene(currentScene);

            Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Payment processing");
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
            controller.setServices(authService, clientService, creditService, paymentService, reportService);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
        } catch (IOException ex) {
            MessageAlert.showErrorMessage(null, ex.getMessage());
        }
    }
}
