package com.atm.bankConnect.enums;

/**
 * All possible transaction types.
 */
public enum MovementType {
    DEPOSIT("DEPOSIT"),
    TRANSFER_IN("TRANSFER IN"),
    TRANSFER_OUT("TRANSFER OUT"),
    WITHDRAW("WITHDRAW");

    private final String VALUE;
    MovementType(String value) {
        VALUE = value;
    }

    public String getVALUE() {
        return VALUE;
    }
}
