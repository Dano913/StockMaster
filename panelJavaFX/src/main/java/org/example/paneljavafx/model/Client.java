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

public class Client {

    private int clientId;
    private Integer gestorId;

    private String name;
    private String surname;
    private String nationalId;

    private String email;
    private String phone;
    private String country;

    private LocalDate joinDate;
}