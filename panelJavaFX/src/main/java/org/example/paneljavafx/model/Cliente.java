package org.example.paneljavafx.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Cliente {

    @JsonProperty("id_cliente")
    private int idCliente;

    // 🔥 compatibilidad con ambos JSON: id_gestor o gestor
    @JsonProperty("id_gestor")
    private Integer idGestorLegacy;

    @JsonProperty("gestor")
    private Integer gestor;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellido")
    private String apellido;

    @JsonProperty("email")
    private String email;

    @JsonProperty("dni")
    private String dni;

    @JsonProperty("fecha_alta")
    private String fechaAlta;

    @JsonProperty("pais")
    private String pais;

    @JsonProperty("posiciones")
    private List<Posicion> posiciones;

    // =========================
    // CONSTRUCTOR
    // =========================
    public Cliente() {}

    // =========================
    // GETTERS
    // =========================
    public int getIdCliente() {
        return idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email;
    }

    public String getDni() {
        return dni;
    }

    public String getFechaAlta() {
        return fechaAlta;
    }

    public String getPais() {
        return pais;
    }

    public List<Posicion> getPosiciones() {
        return posiciones;
    }

    /**
     * 🔥 Devuelve el gestor real aunque venga como id_gestor o gestor
     */
    public int getGestor() {
        if (gestor != null) return gestor;
        if (idGestorLegacy != null) return idGestorLegacy;
        return -1;
    }

    // =========================
    // SETTERS
    // =========================
    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setFechaAlta(String fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public void setPosiciones(List<Posicion> posiciones) {
        this.posiciones = posiciones;
    }

    public void setGestor(Integer gestor) {
        this.gestor = gestor;
    }

    @Override
    public String toString() {
        return String.format("%s %s (ID: %d)", nombre, apellido, idCliente);
    }
}