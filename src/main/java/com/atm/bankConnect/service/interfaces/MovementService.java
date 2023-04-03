package com.atm.bankConnect.service.interfaces;

import com.atm.bankConnect.enums.MovementType;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Movement;

import java.util.List;

public interface MovementService {
    Movement create(double value, MovementType movementType, Account account);
    Movement create(double value, MovementType movementType);
    double getSumAllTodayWithdrawMovements(int accountIdThatOwnsThisMovement);
    Movement deleteAll(Account account);
    void deleteAll(int accountToBeDeletedId);
    List<Movement> getAll();
    List<Movement> getAll(int accountId);
    List<Movement> loadDatabase();
}
