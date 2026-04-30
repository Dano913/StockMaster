package org.example.paneljavafx.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import org.example.paneljavafx.model.Client;
import org.example.paneljavafx.model.ClientFundPosition;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.service.ClientService;
import org.example.paneljavafx.service.GestorService;

import java.util.List;

public class GestorPrivadoViewController {

    // ═══════════════════════
    // HEADER
    // ═══════════════════════
    @FXML private Label labelNombreGestor;
    @FXML private Label labelEmailGestor;

    // ═══════════════════════
    // DATOS
    // ═══════════════════════
    @FXML private Label labelIdGestor;
    @FXML private Label labelDepartamento;
    @FXML private Label labelNumeroClientes;
    @FXML private Label labelPatrimonio;
    @FXML private Label labelRentabilidadMedia;

    // ═══════════════════════
    // TABLE CLIENTES
    // ═══════════════════════
    @FXML private TableView<Client> clientsTable;

    @FXML private TableColumn<Client, String> colClientName;
    @FXML private TableColumn<Client, String> colClientEmail;
    @FXML private TableColumn<Client, String> colClientPortfolio;
    @FXML private TableColumn<Client, String> colClientFundsCount;

    private final ClientService clientService = ClientService.getInstance();
    private final GestorService gestorService = GestorService.getInstance();

    private Gestor gestorActual;

    // ═══════════════════════
    // INIT TABLE
    // ═══════════════════════
    @FXML
    public void initialize() {

        colClientName.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getName())
        );

        colClientEmail.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getEmail())
        );

        colClientPortfolio.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("€%.2f",
                                clientService.calculateWallet((List<ClientFundPosition>) d.getValue())
                        )
                )
        );

        colClientFundsCount.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.valueOf(
                                clientService.countFund((List<ClientFundPosition>) d.getValue())
                        )
                )
        );
    }

    // ═══════════════════════
    // SET GESTOR
    // ═══════════════════════
    public void setGestor(Gestor gestor) {

        this.gestorActual = gestor;

        // HEADER
        labelNombreGestor.setText(gestor.getName() + " " + gestor.getSurname());
        labelEmailGestor.setText(gestor.getEmail());

        // DATOS
        labelIdGestor.setText(String.valueOf(gestor.getGestorId()));

        labelDepartamento.setText(
                "Empresa ID: " + gestor.getCompanyId()
        );

        double patrimonio =
                gestorService.calculateManagedWallet(
                        gestor.getGestorId()
                );

        labelPatrimonio.setText(String.format("€%.2f", patrimonio));

        labelRentabilidadMedia.setText(
                gestor.getYearsOfExperience() + " años exp."
        );

        List<Client> clientesDelGestor = gestorService.getClientesByGestorId(gestor.getGestorId(),
                clientService.getAll());

        clientsTable.setItems(FXCollections.observableArrayList(clientesDelGestor));

        // COUNT
        labelNumeroClientes.setText(String.valueOf(clientesDelGestor.size()));
    }
}