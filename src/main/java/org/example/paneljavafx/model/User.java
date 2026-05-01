package org.example.paneljavafx.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private int id;
    private int userId;

    private String email;
    private String password;

    private String role;
    private String status;

    private Timestamp createdAt;
    private Timestamp updatedAt;
}