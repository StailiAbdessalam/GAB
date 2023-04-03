package com.atm.bankConnect.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Entity class Customer.
 */
public class Customer {
    private int id;
    private String nif, name, password, phone, mobile, email, profession;
    private LocalDate birthDate;


    public Customer(String nif, String name, String password, String phone, String mobile, String email, String profession, LocalDate birthDate) {
        this.nif = nif;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.mobile = mobile;
        this.email = email;
        this.profession = profession;
        this.birthDate = birthDate;
    }

    public Customer(int id, String nif, String name, String password, String phone, String mobile, String email, String profession, LocalDate birthDate) {
        this.id = id;
        this.nif = nif;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.mobile = mobile;
        this.email = email;
        this.profession = profession;
        this.birthDate = birthDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNif() {
        return nif;
    }

    /**
     * Sets a NIF number if it has 9 digits and the first one is different that zero.
     *
     * @see <a href="https://phcgo.net/blog/nif-por-3-vai-ser-realidade/">NIF começado por 3 vai ser realidade em breve</a>
     * @param nif the NIF number
     * @return
     * <ul>
     *     <li>true if NIF is set</li>
     *     <li>false if NIF is not set</li>
     * </ul>
     */
    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    /**
     * Sets the given phone number if it has 9 digits which the first must be equal two or three.
     *
     * @param phone the phone number
     * @return
     * <ul>
     *     <li>true if NIF is set</li>
     *     <li>false if NIF is not set</li>
     * </ul>
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    /**
     * Sets given mobile phone number if it has 9 digits and the first one is equalor three.
     *
     * @param mobile the mobile phone number
     * @return
     * <ul>
     *     <li>true if NIF is set</li>
     *     <li>false if NIF is not set</li>
     * </ul>
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    /**
     * Sets the given e-mail if it has something before and after the @.
     *
     * @param email the e-mail
     * @return
     * <ul>
     *     <li>true if NIF is set</li>
     *     <li>false if NIF is not set</li>
     * </ul>
     */
    public void setEmail(String email) {
            this.email = email;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    @Override
    public String toString() {
        return  "| NIF: " + nif +
                " | NAME: " + name +
                " | PASSWORD: " + password +
                " | PHONE: " + phone +
                " | MOBILE: " + mobile +
                " | E-MAIL: " + email +
                " | PROFESSION: " + profession +
                " | BIRTHDATE: " + birthDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " |";
    }
}
