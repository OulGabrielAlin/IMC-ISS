package org.example.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
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
import org.example.domain.Rate;
import org.example.domain.datatypes.Period;
import org.example.service.CreditService;
import org.example.utils.MessageAlert;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SimulationController {
    private Scene mainScene;
    private CreditService creditService;
    ObservableList<Rate> rateObservableList = FXCollections.observableArrayList();

    @FXML
    private TextField txtSumSimulation;

    @FXML
    private Slider sliderPeriod;

    @FXML
    private Label lblTotalToPay;

    @FXML
    private Label lblPeriod;

    @FXML
    private TableView<Rate> tableScadentar;

    @FXML
    private TableColumn<Rate, Integer> colRateNr;

    @FXML
    private TableColumn<Rate, LocalDate> colDeadline;

    @FXML
    private TableColumn<Rate, Integer> colBaseAmount;

    @FXML
    private TableColumn<Rate, Double> colInterest;

    @FXML
    private TableColumn<Rate, Double> colTotalAmount;


    public void setServices(CreditService creditService)
    {
        this.creditService = creditService;
    }

    public void setMainScene(Scene mainScene)
    {
        this.mainScene = mainScene;
    }

    @FXML
    public void initialize()
    {
        colRateNr.setCellValueFactory(column -> new ReadOnlyObjectWrapper<>(tableScadentar.getItems().indexOf(column.getValue()) + 1));

        colDeadline.setCellValueFactory(new PropertyValueFactory<>("deadline"));
        colBaseAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colInterest.setCellValueFactory(cellData -> {
            Rate rate = cellData.getValue();
            Double interest = rate.getInterest() * 100;
            return new ReadOnlyObjectWrapper<>(interest);
        });

        colTotalAmount.setCellValueFactory(cellData -> {
            Rate rate = cellData.getValue();
            double amount = rate.getAmount();
            double finalAmount = Math.round(amount * (1 + rate.getInterest()));

            return new ReadOnlyObjectWrapper<>(finalAmount);
        });

        sliderPeriod.valueProperty().addListener((observable, oldValue, newValue) -> {
            Integer currentValue = newValue.intValue();
            lblPeriod.setText("Perioada (Luni): " + currentValue);
        });
    }

    @FXML
    public void handleSimulate(ActionEvent event) {
        try {
            String sumText = txtSumSimulation.getText();
            if (sumText == null || sumText.trim().isEmpty()) {
                MessageAlert.showErrorMessage(null, "Va rugam sa introduceti suma dorita.");
                return;
            }

            Double sum = Double.parseDouble(sumText);
            if (sum <= 0) {
                MessageAlert.showErrorMessage(null, "Suma solicitata trebuie sa fie un numar pozitiv!");
                return;
            }

            Integer noOfMonths = (int) sliderPeriod.getValue();
            Period period = new Period();
            period.setNumberOfMonths(noOfMonths);
            period.setStartDate(LocalDate.now());

            List<Rate> generatedRates = creditService.simulateCredit(sum, period);
            rateObservableList.setAll(generatedRates);
            tableScadentar.setItems(rateObservableList);
            calculateTotal(generatedRates);
        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "Suma introdusa nu este un numar valid. Exemplu corect: 5000");
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "An error occured while simulating the credit: " + e.getMessage());
        }
    }


    private void calculateTotal(List<Rate> rates) {
        Double total = 0.0;
        for (Rate rate : rates) {
            total += rate.getAmount();
        }

        total = Math.round(total * 100.0) / 100.0;

        lblTotalToPay.setText("Total: " + total + " RON");
    }

    @FXML
    public void handleBackToMain(ActionEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainScene);
    }
}
