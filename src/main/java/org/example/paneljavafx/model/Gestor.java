package org.example.paneljavafx.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Gestor {

    public enum RiskProfile {
        CONSERVADOR,
        MODERADO,
        AGRESIVO
    }

    private int gestorId;
    private int userId;
    private int companyId;
    private int fundId;
    private int yearsOfExperience;

    private String name;
    private String surname;
    private String nationalId;

    private String email;
    private String phone;

    private RiskProfile riskProfile;
}