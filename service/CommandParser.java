package service;
import model.Command;
import model.TransactionType;

public class CommandParser {

    public static Command parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Command cannot be empty.");
        }

        String[] parts = line.trim().split("\\s+");

        if (parts.length != 3) {
            throw new IllegalArgumentException(
                    "Enter: TYPE ACCOUNT_NUMBER AMOUNT");
        }

        TransactionType type;
        try {
            type = TransactionType.valueOf(
                    parts[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid transaction type.");
        }

        String accountNumber = parts[1];

        long amount;
        try {
            amount = Long.parseLong(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Amount must be a whole number.");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be greater than zero.");
        }

        return new Command(type, accountNumber, amount);
    }
}

