package org.example.paneljavafx.service.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GestorProfileDTO {

    private int idGestor;
    private int idEmpresa;
    private int idFondo;

    private String nationalId;
    private String name;
    private String surname;

    private int yearsOfExperience;
    private String riskProfile;

    private String email;
    private String phone;

    private int userId;
}