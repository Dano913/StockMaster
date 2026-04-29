package org.example.paneljavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gestor {

    public enum PerfilRiesgo {
        CONSERVADOR,
        MODERADO,
        AGRESIVO
    }

    private int idGestor;
    private int idEmpresa;
    private int idFondo;

    private String dni;
    private String nombre;
    private String apellidos;

    private int aniosExperiencia;
    private PerfilRiesgo perfilRiesgo;

    private String email;
    private String telefono;
}