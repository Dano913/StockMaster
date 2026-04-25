package org.example.paneljavafx.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Cliente {
    @JsonProperty("id_cliente")
    private int idCliente;

    @JsonProperty("apellido")
    private String apellido;

    @JsonProperty("fecha_alta")
    private String fechaAlta;

    @JsonProperty("gestor")
    private int gestor;

    private String nombre, email, dni, pais;
    private List<Posicion> posiciones;

    // Constructor vacío (OBLIGATORIO)
    public Cliente() {}

    // =========================
    // GETTERS
    // =========================
    public int getIdCliente() { return idCliente; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getDni() { return dni; }
    public String getFechaAlta() { return fechaAlta; }
    public String getPais() { return pais; }
    public int getGestor() { return gestor; }
    public List<Posicion> getPosiciones() { return posiciones; }

    // =========================
    // SETTERS
    // =========================
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setEmail(String email) { this.email = email; }
    public void setDni(String dni) { this.dni = dni; }
    public void setFechaAlta(String fechaAlta) { this.fechaAlta = fechaAlta; }
    public void setPais(String pais) { this.pais = pais; }
    public void setGestor(int gestor) { this.gestor = gestor; }
    public void setPosiciones(List<Posicion> posiciones) { this.posiciones = posiciones; }

    @Override
    public String toString() {
        return String.format("%s %s (ID: %d)", nombre, apellido, idCliente);
    }
}