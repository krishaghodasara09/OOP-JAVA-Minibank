package service;

import model.CurrentAccount;
import exception.InvalidAmountException;

public class UnsafeDemoAccount extends CurrentAccount {

    public UnsafeDemoAccount() {
        super("Race Condition Test", 0, 0);
    }

    @Override
    public void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive.");
        }

        long oldBalance = balance;
        Thread.yield();
        balance = oldBalance + amount;
    }
}
