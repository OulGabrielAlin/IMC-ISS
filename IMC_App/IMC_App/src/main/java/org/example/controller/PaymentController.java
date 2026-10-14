package org.example.controller;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.domain.Credit;
import org.example.domain.Rate;
import org.example.domain.datatypes.PaymentMethod;
import org.example.domain.datatypes.PaymentStatus;
import org.example.service.CreditService;
import org.example.service.PaymentService;
import org.example.utils.MessageAlert;

import java.time.LocalDate;
import java.util.Collection;

public class PaymentController {
    private Scene mainScene;
    private PaymentService paymentService;

    private ObservableList<Rate> rates = FXCollections.observableArrayList();

    @FXML
    private TextField txtCNPSearch;

    @FXML
    private Button btnSearch;

    @FXML
    private TableView<Rate> tableRateActive;

    @FXML
    private TableColumn<Rate, Integer> colRateNo;

    @FXML
    private TableColumn<Rate, Double> colTotalAmount;

    @FXML
    private TableColumn<Rate, LocalDate> colDeadline;

    @FXML
    private TextField txtDeadline;

    @FXML
    private TextField txtDebt;

    @FXML
    private TextField txtPenalties;

    @FXML
    private TextField txtPaidAmount;

    @FXML
    private ComboBox<PaymentMethod> cbPaymentMethod;

    @FXML
    private Button btnFinalizePayment;

    public void setMainScene(Scene mainScene)
    {
        this.mainScene = mainScene;
    }

    public void setServices(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @FXML
    public void initialize() {
        cbPaymentMethod.getItems().addAll(PaymentMethod.values());

        btnSearch.setOnAction(event -> handleSearchRates());

        btnFinalizePayment.setOnAction(event -> handleRegisterPayment());

        colRateNo.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(tableRateActive.getItems().indexOf(column.getValue()) + 1));
        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        colTotalAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));


        tableRateActive.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showInstallmentDetails(newValue)
        );
    }

    private void handleSearchRates() {
        String cnp = txtCNPSearch.getText();
        Iterable<Rate> unpaidRates = paymentService.getUnpaidRates(cnp);
        rates.setAll((Collection<? extends Rate>) unpaidRates);
        tableRateActive.setItems(rates);
    }

    private void showInstallmentDetails(Rate rate) {
        if (rate != null) {
            txtDeadline.setText(rate.getDeadline().toString());
            txtDebt.setText(String.valueOf(rate.getAmount()));

            if (rate.getPaymentStatus().equals(PaymentStatus.PENALIZED)) {
                txtPenalties.setText(rate.getPenalties().toString());
            }
        } else {
            txtDeadline.clear();
            txtDebt.clear();
            txtPenalties.setText("0.00 RON");
        }
    }

    private void handleRegisterPayment() {
        String amountStr = txtPaidAmount.getText();
        PaymentMethod method = cbPaymentMethod.getValue();

        try {
            Long id = tableRateActive.getSelectionModel().getSelectedItem().getId();
            Double amount =  Double.parseDouble(amountStr);

            paymentService.processPayment(id, amount, method);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Payment", "Successfully processed Payment");
            handleSearchRates();
        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "The submitted amount is not a number");
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "An error occurred while processing the payment " + e.getMessage());
        }
    }

    @FXML
    public void handleBackToMain(ActionEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainScene);
    }
}
