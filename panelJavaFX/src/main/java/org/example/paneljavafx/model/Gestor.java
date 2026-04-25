package org.example.paneljavafx.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Gestor {

    @JsonProperty("id_gestor")
    private int idGestor;

    @JsonProperty("id_empresa")
    private int idEmpresa;

    @JsonProperty("id_fondo")
    private int idFondo;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("apellidos")
    private String apellidos;

    @JsonProperty("anios_experiencia")
    private int aniosExperiencia;

    @JsonProperty("perfil_riesgo")
    private String perfilRiesgo;

    @JsonProperty("email")
    private String email;

    @JsonProperty("telefono")
    private String telefono;

    // =========================
    // CONSTRUCTOR VACÍO (OBLIGATORIO JACKSON)
    // =========================
    public Gestor() {}

    // =========================
    // CONSTRUCTOR COMPLETO
    // =========================
    public Gestor(int idGestor, int idEmpresa, int idFondo,
                  String nombre, String apellidos,
                  int aniosExperiencia, String perfilRiesgo,
                  String email, String telefono) {

        this.idGestor = idGestor;
        this.idEmpresa = idEmpresa;
        this.idFondo = idFondo;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.aniosExperiencia = aniosExperiencia;
        this.perfilRiesgo = perfilRiesgo;
        this.email = email;
        this.telefono = telefono;
    }

    // =========================
    // GETTERS
    // =========================

    public int getIdGestor() {
        return idGestor;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public int getIdFondo() {
        return idFondo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public int getAniosExperiencia() {
        return aniosExperiencia;
    }

    public String getPerfilRiesgo() {
        return perfilRiesgo;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    // =========================
    // SETTERS
    // =========================

    public void setIdGestor(int idGestor) {
        this.idGestor = idGestor;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public void setIdFondo(int idFondo) {
        this.idFondo = idFondo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setAniosExperiencia(int aniosExperiencia) {
        this.aniosExperiencia = aniosExperiencia;
    }

    public void setPerfilRiesgo(String perfilRiesgo) {
        this.perfilRiesgo = perfilRiesgo;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // =========================
    // TOSTRING (BONUS)
    // =========================
    @Override
    public String toString() {
        return String.format("Gestor{id=%d, nombre='%s %s', experiencia=%d, riesgo='%s'}",
                idGestor, nombre, apellidos, aniosExperiencia, perfilRiesgo);
    }
}