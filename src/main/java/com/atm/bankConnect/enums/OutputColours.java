package com.atm.bankConnect.enums;

/**
 * Codes to print coloured font on the console
 *
 * @see <a href="https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println">How to print color in console using System.out.println?</a>
 */
public enum OutputColours {
    /**
     * Reset all configurations
     */

    RESET("\u001B[0m"),
    /**
     * Black regular text
     */
    DARK_GRAY_TEXT_NORMAL("\u001B[38;5;8m"),
    /**
     * Red regular text
     */
    RED_TEXT_NORMAL("\u001B[31m"),
    /**
     * Green regular text
     */
    GREEN_TEXT_NORMAL("\u001B[32m"),
    /**
     * Yellow regular text
     */
    YELLOW_TEXT_NORMAL("\u001B[33m"),
    /**
     * Blue regular text
     */
    BLUE_TEXT_NORMAL("\u001B[34m"),
    /**
     * Purple regular text
     */
    PURPLE_TEXT_NORMAL("\u001B[35m"),
    /**
     * Cyan regular text
     */
    CYAN_TEXT_NORMAL("\u001B[36m"),
    /**
     * White regular text
     */
    WHITE_TEXT_NORMAL("\u001B[37m"),
    /**
     * Red bold text
     */
    RED_TEXT_BOLD("\033[1;31m"),
    /**
     * Green bold text
     */
    GREEN_TEXT_BOLD("\033[1;32m"),
    /**
     * Yellow bold text
     */
    YELLOW_TEXT_BOLD("\033[1;33m"),
    /**
     * Blue bold text
     */
    BLUE_TEXT_BOLD("\033[1;34m"),
    /**
     * Purple bold text
     */
    PURPLE_TEXT_BOLD("\033[1;35m"),
    /**
     * Cyan bold text
     */
    CYAN_TEXT_BOLD("\033[1;36m"),
    /**
     * White bold text
     */
    WHITE_TEXT_BOLD("\033[1;37m"),
    /**
     * Red text in high contrast
     */
    RED_TEXT_BRIGHT("\033[0;91m"),
    /**
     * Green text in high contrast
     */
    GREEN_TEXT_BRIGHT("\033[0;92m"),
    /**
     * Yellow text in high contrast
     */
    YELLOW_TEXT_BRIGHT("\033[0;93m"),
    /**
     * Blue text in high contrast
     */
    BLUE_TEXT_BRIGHT("\033[0;94m"),
    /**
     * Purple text in high contrast
     */
    PURPLE_TEXT_BRIGHT("\033[0;95m"),
    /**
     * Cyan text in high contrast
     */
    CYAN_TEXT_BRIGHT("\033[0;96m"),
    /**
     * White text in high contrast
     */
    WHITE_TEXT_BRIGHT("\033[0;97m"),
    /**
     * Red bold text in high contrast
     */
    RED_TEXT_BRIGHT_BOLD("\033[1;91m"),
    /**
     * Green bold text in high contrast
     */
    GREEN_TEXT_BRIGHT_BOLD("\033[1;92m"),
    /**
     * Yellow bold text in high contrast
     */
    YELLOW_TEXT_BRIGHT_BOLD("\033[1;93m"),
    /**
     * Blue bold text in high contrast
     */
    BLUE_TEXT_BRIGHT_BOLD("\033[1;94m"),
    /**
     * Purple bold text in high contrast
     */
    PURPLE_TEXT_BRIGHT_BOLD("\033[1;95m"),
    /**
     * Cyan bold text in high contrast
     */
    CYAN_TEXT_BRIGHT_BOLD("\033[1;96m"),
    /**
     * White bold text in high contrast
     */
    WHITE_TEXT_BRIGHT_BOLD("\033[1;97m");

    /**
     * Enumeration content value.
     */
    private final String VALUE;
    OutputColours(String VALUE) {
        this.VALUE = VALUE;
    }

    public String getValue() {
        return VALUE;
    }
}
