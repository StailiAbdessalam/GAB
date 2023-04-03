package com.atm.bankConnect.service.interfaces;

import com.atm.bankConnect.model.Customer;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface CustomerService {
    Customer save(Customer customer) throws SQLException;

    Customer getByNif(String nif);
    Customer update(Customer customer);
    void delete(Customer customer);
    List<Customer> getAll();
    Customer getById(int cardMainHolderId);
    List<Customer> loadDatabase();

    boolean validateAge(LocalDate birthDate);
    boolean validateNif(String nif);
    boolean validatePhone(String phone);
    boolean validateMobile(String mobile);
    boolean validateEmail(String email);
}
