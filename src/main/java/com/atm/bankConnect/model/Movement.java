package com.atm.bankConnect.model;

import com.atm.bankConnect.enums.MovementType;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Movement {
    private int id = 1;
    private MovementType type;
    private LocalDate date;
    private double value;
    /**
     * Account that owns this movement
     */
    private Account account;

    public Movement() {
    }

    public Movement(MovementType type, LocalDate date, double value) {
        this.type = type;
        this.date = date;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MovementType getType() {
        return type;
    }

    public void setType(MovementType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "| TYPE: " + type +
                " | DATE: " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " | VALUE: " + new DecimalFormat("0.00").format(value) +
                "€ |";
    }
}
