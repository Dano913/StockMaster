package org.example.stockmaster.javafx.service;

import org.example.stockmaster.javafx.model.TimeRecord;
import org.example.stockmaster.javafx.model.User;

import java.time.LocalDateTime;

public class TimeRecordService {

    private static final TimeRecordService INSTANCE = new TimeRecordService();
    private final UserService userService = UserService.getInstance();

    public static TimeRecordService getInstance() {
        return INSTANCE;
    }

    private int counter = 0;

    public TimeRecord checkIn(User user) {
        return createRecord(user, "IN");
    }

    public TimeRecord checkOut(User user) {
        return createRecord(user, "OUT");
    }

    // =========================
    // CREATE RECORD
    // =========================

    private TimeRecord createRecord(User user, String type) {
        TimeRecord record = new TimeRecord();

        record.setCode(++counter);
        record.setDateTime(LocalDateTime.now());
        record.setType(type);
        record.setUser(user.getName());

        user.addTimeRecord(record);

        userService.save();
        return record;
    }
}