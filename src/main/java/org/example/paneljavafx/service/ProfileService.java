package org.example.paneljavafx.service;

import org.example.paneljavafx.dao.ClientDAO;
import org.example.paneljavafx.dao.GestorDAO;
import org.example.paneljavafx.dao.impl.ClientImpl;
import org.example.paneljavafx.dao.impl.GestorImpl;
import org.example.paneljavafx.model.User;
import org.example.paneljavafx.service.dto.ClientProfileDTO;
import org.example.paneljavafx.service.dto.GestorProfileDTO;

public class ProfileService {

    private final ClientDAO clientDao = new ClientImpl();
    private final GestorDAO gestorDao = new GestorImpl();

    public Object getProfile(User user) {

        return switch (user.getRole().toLowerCase()) {

            case "cliente" -> clientDao.findByUserId(user.getId())
                    .map(c -> new ClientProfileDTO(
                            c.getClientId(),
                            c.getGestorId(),
                            c.getName(),
                            c.getSurname(),
                            c.getEmail(),
                            c.getNationalId(),
                            c.getJoinDate(),
                            c.getCountry(),
                            c.getUserId()
                    ))
                    .orElse(null);

            case "gestor" -> gestorDao.findByUserId(user.getId())
                    .map(g -> GestorProfileDTO.builder()
                            .idGestor(g.getGestorId())
                            .idEmpresa(g.getCompanyId())
                            .idFondo(g.getFundId())
                            .nationalId(g.getNationalId())
                            .name(g.getName())
                            .surname(g.getSurname())
                            .yearsOfExperience(g.getYearsOfExperience())
                            .riskProfile(
                                    g.getRiskProfile() != null
                                            ? g.getRiskProfile().name()
                                            : null
                            )
                            .email(g.getEmail())
                            .phone(g.getPhone())
                            .userId(g.getUserId())
                            .build()
                    )
                    .orElse(null);

            default -> null;
        };
    }
}