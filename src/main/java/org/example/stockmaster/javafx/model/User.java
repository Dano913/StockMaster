package org.example.stockmaster.javafx.model;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    private String id;
    private String name;
    private String email;
    private String age;
    private String gender;
    private String phone;
    private String role;
    private String password;
    private String profile;
    private String dni;

    private List<TimeRecord> timeRecords = new ArrayList<>();

    public void addTimeRecord(TimeRecord record) {
        this.timeRecords.add(record);
    }
}