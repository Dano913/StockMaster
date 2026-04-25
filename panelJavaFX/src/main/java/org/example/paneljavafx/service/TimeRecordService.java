package org.example.paneljavafx.service;

import org.example.paneljavafx.model.Gestor;
import org.example.paneljavafx.model.TimeRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TimeRecordService {

    private static final TimeRecordService INSTANCE = new TimeRecordService();

    public static TimeRecordService getInstance() {
        return INSTANCE;
    }

    // =========================
    // DATA STORE (GLOBAL)
    // =========================
    private final List<TimeRecord> records = new ArrayList<>();

    private int counter = 0;

    // =========================
    // CHECK IN / OUT
    // =========================
    public TimeRecord checkIn(Gestor gestor) {
        return createRecord(gestor, "IN");
    }

    public TimeRecord checkOut(Gestor gestor) {
        return createRecord(gestor, "OUT");
    }

    // =========================
    // CREATE RECORD
    // =========================
    private TimeRecord createRecord(Gestor gestor, String type) {

        TimeRecord record = new TimeRecord();

        record.setCode(++counter);
        record.setDateTime(LocalDateTime.now());
        record.setType(type);
        record.setUser(gestor.getNombre());

        records.add(record);

        return record;
    }

    // =========================
    // GET ALL RECORDS
    // =========================
    public List<TimeRecord> getAllRecords() {
        return records;
    }
}