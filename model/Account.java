package model;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;

public abstract class Account implements Transactable, InterestBearing {

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
    public void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than zero.");
        }
        balance += amount;
    }

    @Override
    public boolean withdraw(long amount)
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
            to.deposit(amount);

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
    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
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
