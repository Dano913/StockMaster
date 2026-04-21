package org.example.stockmaster.javafx.model;

import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeRecord {

    private int code;
    private LocalDateTime dateTime;
    private String type;
    private String user;

    @Override
    public String toString() {
        return type + " - " +
                dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }
}