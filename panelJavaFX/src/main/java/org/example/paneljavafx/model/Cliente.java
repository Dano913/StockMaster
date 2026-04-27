package org.example.paneljavafx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cliente {

    // =========================
    // SETTERS
    // =========================
    // =========================
    // GETTERS
    // =========================
    @Setter
    @Getter
    @JsonProperty("id_cliente")
    private int idCliente;

    @JsonProperty("id_gestor")
    private Integer idGestor;

    @Setter
    @Getter
    @JsonProperty("nombre")
    private String nombre;

    @Setter
    @Getter
    @JsonProperty("apellido")
    private String apellido;

    @Setter
    @Getter
    @JsonProperty("email")
    private String email;

    @Setter
    @Getter
    @JsonProperty("dni")
    private String dni;

    @Setter
    @Getter
    @JsonProperty("fecha_alta")
    private String fechaAlta;

    @Getter
    @Setter
    @JsonProperty("pais")
    private String pais;

    @Setter
    @Getter
    @JsonProperty("posiciones")
    private List<Posicion> posiciones;

    // =========================
    // CONSTRUCTOR
    // =========================
    public Cliente() {}

    public int getIdGestor() {
        return idGestor != null ? idGestor : -1;
    }

    @Override
    public String toString() {
        return String.format("%s %s (ID: %d)", nombre, apellido, idCliente);
    }
}