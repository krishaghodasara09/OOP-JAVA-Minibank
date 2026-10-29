package model;

import java.io.Serializable;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;

public abstract class Account
        implements Transactable, InterestBearing,
                   Serializable, Comparable<Account> {

    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;

    @MaxLength(50)
    private String ownerName;

    @Positive
    protected long balance;

    private boolean active;

    private static int accountCounter = 0;

    private static String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    // Prevent duplicate account numbers after loading saved accounts
    public static void synchronizeAccountCounter(
            Iterable<Account> existingAccounts) {

        int maxNumber = accountCounter;

        for (Account account : existingAccounts) {
            String number = account.getAccountNumber();

            if (number != null && number.matches("AC\\d+")) {
                int value = Integer.parseInt(number.substring(2));
                maxNumber = Math.max(maxNumber, value);
            }
        }

        accountCounter = maxNumber;
    }

    public Account(String ownerName, long openingBalance) {
        if (openingBalance < 0) {
            throw new IllegalArgumentException(
                    "Opening balance cannot be negative.");
        }

        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = openingBalance;
        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    @Override
    public synchronized void deposit(long amount)
            throws InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than zero.");
        }

        if (balance > Long.MAX_VALUE - amount) {
            throw new InvalidAmountException(
                    "Deposit exceeds the maximum supported balance.");
        }

        balance += amount;
    }

    @Override
    public synchronized boolean withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be greater than zero.");
        }

        if (!canWithdraw(amount)) {
            throw new InsufficientFundsException(
                    "Insufficient funds or account withdrawal limit exceeded.",
                    withdrawalShortfall(amount));
        }

        balance -= amount;
        return true;
    }

    protected long withdrawalShortfall(long amount) {
        return Math.max(0, amount - balance);
    }

    public void transfer(Account to, long amount) throws BankException {
        try {
            if (to == null) {
                throw new AccountNotFoundException(
                        "Destination account was not found.");
            }

            if (to == this) {
                throw new BankException(
                        "Cannot transfer money to the same account.");
            }

            withdraw(amount);

            try {
                to.deposit(amount);
            } catch (InvalidAmountException e) {
                try {
                    deposit(amount);
                } catch (InvalidAmountException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }

            System.out.println("Transfer successful: Rs. " + amount);

        } catch (BankException e) {
            throw e;
        } finally {
            System.out.println("Transfer operation finished.");
        }
    }

    public abstract boolean canWithdraw(long amount);

    @Override
    public abstract double interestRate();

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public synchronized long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public int compareTo(Account other) {
        return this.accountNumber.compareTo(other.accountNumber);
    }

    @Override
    public String toString() {
        return "Account Number: " + accountNumber
                + ", Owner: " + ownerName
                + ", Balance: Rs. " + balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Account other)) {
            return false;
        }

        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }
}
