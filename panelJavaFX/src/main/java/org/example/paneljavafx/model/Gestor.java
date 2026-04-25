package org.example.paneljavafx.model;

import java.util.Arrays;

public class Gestor {

    private int id_gestor;
    private int id_empresa;
    private int id_fondo;

    private String nombre;
    private String apellidos;

    private int anios_experiencia;
    private String perfil_riesgo;

    private String email;
    private String telefono;

    public Gestor() {}

    public Gestor(int id_gestor, int id_empresa, int id_fondo,
                  String nombre, String apellidos,
                  int anios_experiencia, String perfil_riesgo,
                  String email, String telefono) {

        this.id_gestor = id_gestor;
        this.id_empresa = id_empresa;
        this.id_fondo = id_fondo;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.anios_experiencia = anios_experiencia;
        this.perfil_riesgo = perfil_riesgo;
        this.email = email;
        this.telefono = telefono;
    }

    public String getNombre() {
        return "";
    }

    public String getEmail() {
        return "";
    }

    public String getId_gestor() {
        return "";
    }

    public String getPerfil_riesgo() {
        return "";
    }

    public Arrays getTimeRecords() {
        return null;
    }

    public void addTimeRecord(TimeRecord record) {
    }

    public char[] getAnios_experiencia() {
        return new char[0];
    }

    // getters y setters...
}