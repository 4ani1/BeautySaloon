package com.example.deepthort;

import java.time.LocalDate;
import java.util.Date;

public class Employee {
    private int id;
    private String surName;
    private String name;
    private String lastName;
    private String speciality;
    private String number;
    private LocalDate brithDate;
    private LocalDate receiptDate;
    private String status;

    public Employee(String surName, String name, String lastName, String speciality, String number, LocalDate brithDate, LocalDate receiptDate, String status) {
        this.surName = surName;
        this.name = name;
        this.lastName = lastName;
        this.speciality = speciality;
        this.number = number;
        this.brithDate = brithDate;
        this.receiptDate = receiptDate;
        this.status = status;
    }

    public Employee(int id, String surName) {
        this.id = id;
        this.surName = surName;
    }

    public String getSurName() {
        return surName;
    }

    public int getID() { return id; }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) { this.number = number; }

    public LocalDate getBrithDate() {
        return brithDate;
    }

    public void setBrithDate(LocalDate brithDate) {
        this.brithDate = brithDate;
    }

    public LocalDate getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(LocalDate receiptDate) {
        this.receiptDate = receiptDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
