package com.atm.bankConnect.service.implementations.list;

import com.atm.bankConnect.enums.MovementType;
import com.atm.bankConnect.model.Account;
import com.atm.bankConnect.model.Movement;
import com.atm.bankConnect.repository.interfaces.MovementRepository;
import com.atm.bankConnect.service.interfaces.MovementService;

import java.time.LocalDate;
import java.util.List;

public class MovementListServiceImplementation implements MovementService {
    private MovementRepository movementRepositoryImplementation;

    public MovementListServiceImplementation(MovementRepository movementRepositoryImplementation) {
        this.movementRepositoryImplementation = movementRepositoryImplementation;
    }

    @Override
    public Movement create(double value, MovementType movementType, Account account) {
        // Used only on JDBC
        return null;
    }

    @Override
    public Movement create(double value, MovementType movementType) {
        Movement movement = new Movement();
        movement.setDate(LocalDate.now());
        movement.setValue(value);
        movement.setType(movementType);

        return movementRepositoryImplementation.create(movement);
    }

    @Override
    public double getSumAllTodayWithdrawMovements(int accountIdThatOwnsThisMovement) {
        return 0;
    }

    @Override
    public Movement deleteAll(Account account) {
        return null;
    }

    @Override
    public void deleteAll(int accountToBeDeletedId) {
//        Used only on JDBC
    }

    @Override
    public List<Movement> getAll() {
        return movementRepositoryImplementation.findAll();
    }

    @Override
    public List<Movement> getAll(int accountId) {
        // Used only on JDBC
        return null;
    }

    @Override
    public List<Movement> loadDatabase() {
        return movementRepositoryImplementation.loadDatabase();
    }
}
