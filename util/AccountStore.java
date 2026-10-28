package util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import model.Account;

public class AccountStore {

    private final HashMap<String, Account> accounts = new HashMap<>();
    private final ArrayList<String> transactionHistory = new ArrayList<>();

    // Add an account using its account number as the key
    public void add(Account account) {
        if (account == null) {
            throw new IllegalArgumentException(
                    "Account cannot be null.");
        }

        accounts.put(account.getAccountNumber(), account);
    }

    // Find an account using its account number
    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // Return all accounts
    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    // Store a transaction in the history
    public void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    // Return transaction history
    public ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    // Return accounts sorted by balance, from lowest to highest
    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> sortedAccounts = getAll();

        sortedAccounts.sort(
                Comparator.comparingLong(Account::getBalance));

        return sortedAccounts;
    }
}

