package org.example.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.domain.Administrator;
import org.example.service.*;
import org.example.service.dto.FinancialReportDTO;
import org.example.utils.MessageAlert;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class DashboardAdminController {
    private AuthService authService;
    private ClientService clientService;
    private CreditService creditService;
    private PaymentService paymentService;
    private Administrator user;
    private ReportService reportService;
    private ObservableList<FinancialReportDTO> tableData = FXCollections.observableArrayList();

    @FXML
    private ComboBox<String> cbReportType;

    @FXML
    private DatePicker dpStart;

    @FXML
    private DatePicker dpEnd;

    @FXML
    private ComboBox<String> cbAgents;

    @FXML
    private Button btnGenerateReport;

    @FXML
    private Button btnExportCSV;


    @FXML
    private TableView<FinancialReportDTO> tableReport;

    @FXML
    private TableColumn<FinancialReportDTO, LocalDate> colReportDate;

    @FXML
    private TableColumn<FinancialReportDTO, Integer> colActiveCredits;

    @FXML
    private TableColumn<FinancialReportDTO, Double> colLoanedAmount;

    @FXML
    private TableColumn<FinancialReportDTO, Double> colCollectedAmount;

    @FXML
    private TableColumn<FinancialReportDTO, Integer> colDelayedPayments;

    @FXML
    private Button btnLogout;

    public void setServicesAndUser(AuthService authService, ClientService clientService, CreditService creditService, PaymentService paymentService, ReportService reportService, Administrator user) {
        this.authService = authService;
        this.clientService = clientService;
        this.creditService = creditService;
        this.paymentService = paymentService;
        this.reportService = reportService;
        this.user = user;
    }

    public void initialize() {
        cbReportType.getItems().addAll("Raport general");
        cbReportType.getSelectionModel().selectFirst();

        cbAgents.getItems().addAll("All agents");
        cbAgents.getSelectionModel().selectFirst();

        dpStart.setValue(LocalDate.now().withDayOfMonth(1));
        dpEnd.setValue(LocalDate.now());

        btnGenerateReport.setOnAction(event -> handleGenerateReport());
        btnExportCSV.setOnAction(event -> handleExportCSV());

         colReportDate.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(LocalDate.now()));
         colActiveCredits.setCellValueFactory(new PropertyValueFactory<>("totalActiveCredits"));
         colLoanedAmount.setCellValueFactory(new PropertyValueFactory<>("totalLoanAmount"));
         colCollectedAmount.setCellValueFactory(new PropertyValueFactory<>("totalCollectedAmount"));
         colDelayedPayments.setCellValueFactory(new PropertyValueFactory<>("totalOverdueRates"));

         tableReport.setItems(tableData);
    }

    private void handleGenerateReport() {
        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();

        if (start == null || end == null || start.isAfter(end)) {
            MessageAlert.showErrorMessage(null, "Va rugam să selectați un interval de timp valid.");
            return;
        }

        try {
            FinancialReportDTO report = reportService.generateFinancialReport(start, end);
            tableData.add(report);

            if (tableData.isEmpty()) {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Raport Gol", "Nu există date pentru perioada selectată.");
            }
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "Eroare la generarea raportului: " + e.getMessage());
        }
    }

    private void handleExportCSV() {
        if (tableData.isEmpty()) {
            MessageAlert.showErrorMessage(null, "Nu exista date în tabel pentru a fi exportate.");
            return;
        }

        FinancialReportDTO selectedReport = tableReport.getSelectionModel().getSelectedItem();

        if (selectedReport == null) {
            MessageAlert.showErrorMessage(null, "Va rugam sa selectati un raport pentru a fi exportat in format CSV.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvează Raport Excel/CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("Raport_Activitate_" + LocalDate.now() + ".csv");

        File file = fileChooser.showSaveDialog(btnExportCSV.getScene().getWindow());

        if (file != null) {
            try {
                reportService.exportToCSV(selectedReport, file.getAbsolutePath());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Raportul a fost salvat cu succes la: " + file.getAbsolutePath());
            } catch (Exception e) {
                MessageAlert.showErrorMessage(null, "Eroare la scrierea fișierului: " + e.getMessage());
            }
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
