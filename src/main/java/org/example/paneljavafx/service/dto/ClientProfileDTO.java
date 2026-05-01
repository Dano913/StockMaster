package org.example.paneljavafx.service.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientProfileDTO {

    private int clientId;
    private int gestorId;

    private String name;
    private String surname;
    private String email;
    private String dni;

    private LocalDate joinDate;
    private String country;

    private int userId;
}