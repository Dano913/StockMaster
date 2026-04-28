package org.example.paneljavafx.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import org.example.paneljavafx.model.Cliente;
import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.service.ClienteService;
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
    @FXML private TableView<Cliente> clientsTable;

    @FXML private TableColumn<Cliente, String> colClientName;
    @FXML private TableColumn<Cliente, String> colClientEmail;
    @FXML private TableColumn<Cliente, String> colClientPortfolio;
    @FXML private TableColumn<Cliente, String> colClientFundsCount;

    private final ClienteService clienteService = ClienteService.getInstance();
    private final GestorService gestorService = GestorService.getInstance();

    private Gestor gestorActual;

    // ═══════════════════════
    // INIT TABLE
    // ═══════════════════════
    @FXML
    public void initialize() {

        colClientName.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getNombre())
        );

        colClientEmail.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().getEmail())
        );

        colClientPortfolio.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("€%.2f",
                                clienteService.calcularCartera(d.getValue())
                        )
                )
        );

        colClientFundsCount.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.valueOf(
                                clienteService.contarFondosUnicos(d.getValue())
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
        labelNombreGestor.setText(gestor.getNombre() + " " + gestor.getApellidos());
        labelEmailGestor.setText(gestor.getEmail());

        // DATOS
        labelIdGestor.setText(String.valueOf(gestor.getIdGestor()));

        labelDepartamento.setText(
                "Empresa ID: " + gestor.getIdEmpresa()
        );

        double patrimonio =
                gestorService.calcularPatrimonioGestionado(
                        gestor.getIdGestor(),
                        clienteService.getAll()
                );

        labelPatrimonio.setText(String.format("€%.2f", patrimonio));

        labelRentabilidadMedia.setText(
                gestor.getAniosExperiencia() + " años exp."
        );

        List<Cliente> clientesDelGestor = gestorService.getClientesByGestorId(gestor.getIdGestor(),
                clienteService.getAll());

        clientsTable.setItems(FXCollections.observableArrayList(clientesDelGestor));

        // COUNT
        labelNumeroClientes.setText(String.valueOf(clientesDelGestor.size()));
    }
}