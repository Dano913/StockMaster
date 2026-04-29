package org.example.paneljavafx.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    private int idCliente;

    private Integer idGestor;

    private String nombre;

    private String apellido;

    private String email;

    private String dni;

    private LocalDate fechaAlta;

    private String pais;

    private List<Posicion> posiciones;
}