package org.example.paneljavafx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class Fund {

    private String fundId;
    private String companyId;

    private String name;
    private String isinCode;
    private String type;
    private String category;
    private String currency;

    private LocalDate createdAt;

}