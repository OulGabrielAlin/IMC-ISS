package org.example.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.domain.AgentCredit;
import org.example.domain.Client;
import org.example.domain.datatypes.Adress;
import org.example.service.AuthService;
import org.example.service.ClientService;
import org.example.utils.MessageAlert;

import java.time.LocalDate;

public class ClientManagementController {
    private AuthService authService;
    private ClientService clientService;
    private AgentCredit user;
    private Scene mainScene;

    private ObservableList<Client> model = FXCollections.observableArrayList();

    @FXML
    private TextField txtCautare;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnNewClient;

    @FXML
    private Button btnDeleteClient;

    @FXML
    private TableView<Client> tableClienti;

    @FXML
    private TableColumn<Client, String> clientiColumnCNP;

    @FXML
    private TableColumn<Client, String> clientiColumnNume;

    @FXML
    private TextField txtNume;

    @FXML
    private TextField txtPrenume;

    @FXML
    private TextField txtCNP;

    @FXML
    private TextField txtVenit;

    @FXML
    private TextField txtStrada;

    @FXML
    private TextField txtNr;

    @FXML
    private TextField txtLocalitate;

    @FXML
    private TextField txtJudet;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnSaveModifications;

    @FXML
    private DatePicker datePicker;

    public void setServicesAndUser(AuthService authService, ClientService clientService, AgentCredit user)
    {
        this.authService = authService;
        this.clientService = clientService;
        this.user = user;
        loadData();
    }

    public void setMainScene(Scene mainScene)
    {
        this.mainScene = mainScene;
    }

    @FXML
    public void initialize()
    {
        clientiColumnCNP.setCellValueFactory(cellData -> {
            return new SimpleObjectProperty<>(cellData.getValue().getCnp());
        });

        clientiColumnNume.setCellValueFactory(cellData -> {
            return new SimpleObjectProperty<>(cellData.getValue().getLastName() + " " + cellData.getValue().getFirstName());
        });
        tableClienti.setItems(model);

        configureSearch();

        tableClienti.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                showClientDetails(newValue);
            } else {
                clearFields();
            }
        });
    }

    private void configureSearch() {
        FilteredList<Client> filteredData = new FilteredList<>(model, p -> true);

        txtCautare.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(client -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (client.getCnp() != null && client.getCnp().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                String fullName = client.getLastName() + " " + client.getFirstName();
                return fullName.toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Client> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableClienti.comparatorProperty());

        tableClienti.setItems(sortedData);
    }

    private void loadData() {
        model.clear();
        model.addAll(clientService.findAllClients());
    }

    @FXML
    public void handleResetFields() {
        clearFields();
    }

    private void clearFields() {
        txtCNP.setText("");
        txtVenit.setText("");
        txtStrada.setText("");
        txtCNP.setEditable(true);
        txtNr.setText("");
        datePicker.setValue(null);
        txtLocalitate.setText("");
        txtJudet.setText("");
        txtPrenume.setText("");
        txtNume.setText("");
    }

    private void showClientDetails(Client newValue) {
        txtNume.setText(newValue.getLastName());
        txtPrenume.setText(newValue.getFirstName());
        txtCNP.setText(newValue.getCnp());
        txtCNP.setEditable(false);
        txtVenit.setText(newValue.getSalary().toString());
        datePicker.setValue(newValue.getDateOfBirth());
        txtStrada.setText(newValue.getAdress().getStreet());
        txtNr.setText(newValue.getAdress().getNumber());
        txtLocalitate.setText(newValue.getAdress().getCity());
        txtJudet.setText(newValue.getAdress().getCounty());
    }

    @FXML
    public void handleAddNewClient() {
        String nume = txtNume.getText();
        String prenume = txtPrenume.getText();
        String cnp = txtCNP.getText();
        String venitString = txtVenit.getText();
        Double venit;

        try {
            venit = Double.parseDouble(txtVenit.getText());
        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "Income must be a number");
            return;
        }

        LocalDate dateOfBirth = datePicker.getValue();

        String strada = txtStrada.getText();
        String nrString = txtNr.getText();
        Integer nr;

        try {
            nr =  Integer.parseInt(txtNr.getText());
        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "Street nr should be a natural number");
            return;
        }

        String localitate = txtLocalitate.getText();
        String judet = txtJudet.getText();

        Client client = new Client();
        client.setLastName(nume);
        client.setFirstName(prenume);
        client.setCnp(cnp);
        client.setSalary(venit);
        client.setDateOfBirth(dateOfBirth);
        Adress adress = new Adress(strada, nr.toString(), localitate);
        adress.setCounty(judet);
        adress.setCountry("Romania");
        client.setAdress(adress);

        try {
            clientService.registerClient(client);
            model.add(client);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Confirmation", "The client has been registered!");
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    @FXML
    public void handleDeleteClient() {
        Client selectedClient = tableClienti.getSelectionModel().getSelectedItem();

        if (selectedClient != null) {
            boolean confirmed = MessageAlert.showConfirmation(null, "Are you sure you want to delete the client: " + selectedClient.getLastName() + " " + selectedClient.getFirstName() + "?");

            if (confirmed) {
                try {
                    clientService.removeClient(selectedClient.getId());
                    model.remove(selectedClient);
                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", "The client has been removed!");
                } catch (Exception e) {
                    MessageAlert.showErrorMessage(null, e.getMessage());
                }
            }
        } else {
            MessageAlert.showErrorMessage(null, "Select a client first!");
        }
    }

    @FXML
    public void handleSaveModifications() {
        Client selectedClient = tableClienti.getSelectionModel().getSelectedItem();
        if (selectedClient == null) {
            MessageAlert.showErrorMessage(null, "Select a client first!");
            return;
        }

        Long id = selectedClient.getId();
        String nume = txtNume.getText();
        String prenume = txtPrenume.getText();
        String cnp = selectedClient.getCnp();
        String venitString = txtVenit.getText();
        Double venit;

        try {
            venit = Double.parseDouble(venitString);
        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "Income must be a number");
            return;
        }

        LocalDate dateOfBirth = datePicker.getValue();

        String strada = txtStrada.getText();
        String nrString = txtNr.getText();
        Integer nr;

        try {
            nr =  Integer.parseInt(nrString);
        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "Street nr should be a natural number");
            return;
        }

        String localitate = txtLocalitate.getText();
        String judet = txtJudet.getText();

        Client client = new Client();
        client.setId(id);
        client.setLastName(nume);
        client.setFirstName(prenume);
        client.setCnp(cnp);
        client.setSalary(venit);
        client.setDateOfBirth(dateOfBirth);
        Adress adress = new Adress(strada, nr.toString(), localitate);
        adress.setCounty(judet);
        adress.setCountry("Romania");
        client.setAdress(adress);

        try {
            clientService.updateClientProfile(client);
            loadData();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Confirmation", "The client has been updated!");
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    @FXML
    public void handleBackToMain(ActionEvent event) {
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(mainScene);
    }
}
