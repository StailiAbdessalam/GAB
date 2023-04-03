package com.atm.bankConnect.service.implementations.jdbc;

import com.atm.bankConnect.enums.MovementType;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Movement;
import com.atm.bankConnect.repository.interfaces.MovementRepository;
import com.atm.bankConnect.service.interfaces.MovementService;

import java.util.List;

public class MovementJDBCServiceImplementation implements MovementService {
    private MovementRepository movementJDBCRepositoryImplementation;

    public MovementJDBCServiceImplementation(MovementRepository movementJDBCRepositoryImplementation) {
        this.movementJDBCRepositoryImplementation = movementJDBCRepositoryImplementation;
    }

    @Override
    public double getSumAllTodayWithdrawMovements(int accountIdThatOwnsThisMovement) {
        return movementJDBCRepositoryImplementation.sumAllTodayWithdrawMovements(accountIdThatOwnsThisMovement);
    }

    @Override
    public Movement deleteAll(Account account) {
        return null;
    }

    @Override
    public void deleteAll(int accountToBeDeletedId) {
        movementJDBCRepositoryImplementation.deleteAll(accountToBeDeletedId);
    }

    @Override
    public List<Movement> getAll() {
//        Used Only on Lists
        return null;
    }

    @Override
    public List<Movement> getAll(int accountId) {
        return movementJDBCRepositoryImplementation.findAll(accountId);
    }

    @Override
    public List<Movement> loadDatabase() {
        return movementJDBCRepositoryImplementation.loadDatabase();
    }
    @Override
    public Movement create(double value, MovementType movementType, Account account) {
        Movement movement = new Movement();
        movement.setValue(value);
        movement.setType(movementType);
        movement.setAccount(account);
        return movementJDBCRepositoryImplementation.create(movement);
    }

    @Override
    public Movement create(double value, MovementType movementType) {
        // Used only on Lists
        return null;
    }
}
