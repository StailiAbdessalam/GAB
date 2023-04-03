package com.atm.bankConnect.service.implementations.jdbc;

import com.atm.bankConnect.model.Customer;
import com.atm.bankConnect.repository.interfaces.CustomerRepository;
import com.atm.bankConnect.service.interfaces.CustomerService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Contains all methods responsible for the businees rules related to customers.
 */
public class CustomerJDBCServiceImplementation implements CustomerService {
    /**
     * Contains all methods from the persistence layer.
     */
    private CustomerRepository customerRepositoryImplementation;

    public CustomerJDBCServiceImplementation(CustomerRepository customerRepository) {
        customerRepositoryImplementation = customerRepository;
    }


    @Override
    public Customer save(Customer customer) throws SQLException {
        return customerRepositoryImplementation.create(customer);
    }

    @Override
    public Customer update(Customer customer) {
        return customerRepositoryImplementation.update(customer);
    }

    /**
     * Gets a specific customer that owns the given NIF number.
     *
     * @return the <code>Customer</code> object
     */
    public Customer getByNif(String nif) {
        return customerRepositoryImplementation.findByNif(nif);
    }

    /**
     * Deletes a customer with a given NIF number.<br>
     * <em>Allows returning to main menu typing 0</em>
     */
    public void delete(Customer customer) {
        customerRepositoryImplementation.delete(customer);
    }

    /**
     * Displays all customers.
     */
    public List<Customer> getAll() {
        return customerRepositoryImplementation.findAll();
    }

    @Override
    public Customer getById(int cardMainHolderId) {
        return customerRepositoryImplementation.findById(cardMainHolderId);
    }

    public boolean validateNif(String nif) {
        return Boolean.FALSE.equals(customerRepositoryImplementation.verifyIfNifAlreadyExists(nif)) && nif.matches("^[1-9][0-9]{8}$");
    }

    public boolean validatePhone(String phone) {
        return phone.matches("^[2-3][0-9]{8}$");
    }

    public boolean validateMobile(String mobile) {
        return mobile.matches("^(9)[0-9]{8}$");
    }

    public boolean validateEmail(String email) {
        return email.matches("^(.+)@(.+)$");
    }

    public boolean validateAge(LocalDate birthDate) {
        LocalDate today = LocalDate.now();
        int age = Period.between(birthDate, today).getYears();
        return age >= 18;
    }

    /**
     * Generates initial data to fill the Arraylist that's serves as database.
     */
    @Override
    public List<Customer> loadDatabase() {
        return customerRepositoryImplementation.loadDatabase();
    }
}
