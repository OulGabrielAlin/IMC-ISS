package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.domain.Client;
import org.example.domain.CreditApplication;
import org.example.domain.datatypes.CreditApplicationStatus;
import org.example.domain.datatypes.Period;
import org.example.service.ClientService;
import org.example.service.CreditService;
import org.example.service.EligibilityService;
import org.example.service.dto.EligibilityResult;
import org.example.utils.MessageAlert;

import java.time.LocalDate;

public class CreditCreationController {
    private ClientService clientService;
    private CreditService creditService;
    private Scene mainScene;
    private Client currentClient = null;

    @FXML
    private TextField txtCNPSearch;

    @FXML
    private Button btnVerify;

    @FXML
    private TextField txtClientName;

    @FXML
    private ProgressBar pbScoring;

    @FXML
    private TextField txtRequestedSum;

    @FXML
    private TextField txtDurataAprobata;

    @FXML
    private ComboBox<String> cbDestination;

    @FXML
    private TextArea txtObservatii;

    public void setServices(ClientService clientService, CreditService creditService)
    {
        this.clientService = clientService;
        this.creditService = creditService;
    }

    public void setMainScene(Scene mainScene)
    {
        this.mainScene = mainScene;
    }

    @FXML
    public void initialize() {
        cbDestination.getItems().addAll("Nevoi personale", "Achizitii imobiliare", "Achizitii auto", "Refinantare", "Altele");
        pbScoring.setProgress(0.0);
        btnVerify.setOnAction(this::handleClientVerification);
    }

    private void handleClientVerification(ActionEvent event) {
        String cnp = txtCNPSearch.getText();

        if (cnp == null || cnp.trim().isEmpty()) {
            MessageAlert.showErrorMessage(null, "Vă rugăm să introduceți un CNP pentru căutare.");
            return;
        }

        try {
            if (cnp.length() == 13) {
                currentClient = clientService.findClientByCNP(cnp);

                txtClientName.setText(currentClient.getLastName() + " " + currentClient.getFirstName());
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Găsit", "Clientul a fost identificat cu succes.");
            } else {
                MessageAlert.showErrorMessage(null, "CNP invalid sau client inexistent în baza de date.");
                currentClient = null;
                txtClientName.clear();
            }
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "CNP invalid sau client inexistent");
        }
    }

    @FXML
    public void handleCreateCreditApplication(ActionEvent event) {
        if (currentClient == null) {
            MessageAlert.showErrorMessage(null, "Va rugam să verificați/selectați un client inainte de a trimite cererea.");
            return;
        }

        if (txtRequestedSum.getText().isEmpty() || txtDurataAprobata.getText().isEmpty()) {
            MessageAlert.showErrorMessage(null, "Va rugăm sa completați suma si durata creditului.");
            return;
        }

        try {
            Double sum = Double.parseDouble(txtRequestedSum.getText());
            Integer months = Integer.parseInt(txtDurataAprobata.getText());
            String dest = cbDestination.getValue();
            String obs = txtObservatii.getText();

            CreditApplication newApplication = new CreditApplication();
            newApplication.setClient(currentClient);
            newApplication.setRequestedSum(sum);
            newApplication.setPeriod(new Period(LocalDate.now(), months));
            newApplication.setStatus(CreditApplicationStatus.IN_ANALYSIS);

             newApplication.setDestination(dest);
             newApplication.setObservations(obs);

            EligibilityResult result = creditService.registerCreditApplication(newApplication);
            showResult(result, newApplication.getStatus());

            if (result.getEligible() == true) {
                creditService.finalizeContract(newApplication.getId());
            }

        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "Valorile pentru Suma și Durata trebuie să fie numerice!");
        } catch (IllegalArgumentException e) {
            MessageAlert.showErrorMessage(null, "Eroare de validare: " + e.getMessage());
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "A aparut o eroare neașteptata: " + e.getMessage());
        }
    }

    private void showResult(EligibilityResult result, CreditApplicationStatus status) {
        double normalizedScore = result.getScore() / 100.0;
        pbScoring.setProgress(normalizedScore);

        if (status == CreditApplicationStatus.APPROVED) {
            pbScoring.setStyle("-fx-accent: #2ecc71;");
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Aprobat",
                    "Cererea a fost APROBATA!\nDetalii: " + result.getMessage());

            clearForm();
        } else {
            pbScoring.setStyle("-fx-accent: #e74c3c;");
            MessageAlert.showErrorMessage(null,
                    "Cererea a fost RESPINSA!\nMotiv: " + result.getMessage());
        }
    }

    @FXML
    private void clearForm() {
        txtCNPSearch.clear();
        txtClientName.clear();
        txtRequestedSum.clear();
        txtDurataAprobata.clear();
        cbDestination.getSelectionModel().clearSelection();
        txtObservatii.clear();
        pbScoring.setProgress(0.0);
        currentClient = null;
    }

    @FXML
    public void handleBackToMain(ActionEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainScene);
    }
}
