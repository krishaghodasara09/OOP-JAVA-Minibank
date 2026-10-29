import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import util.AccountStore;
import util.ReportGenerator;
import util.StatePersister;
import util.TransactionLog;

public class MiniBank {

    private static final Path ACCOUNT_FILE =
            Path.of("data", "accounts.dat");

    private static final Path LOGS_FOLDER =
            Path.of("logs");

    private static final Path TRANSACTION_FILE =
            LOGS_FOLDER.resolve("transactions.log");

    private static final Path REPORT_FILE =
            Path.of("reports", "end-of-day-report.txt");

    private static final Scanner sc = new Scanner(System.in);

    private static final AccountStore store = new AccountStore();

    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("       WELCOME TO MINIBANK");
        System.out.println("================================");

        loadAccounts();

        boolean running = true;

        while (running) {
            showMenu();

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> openAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> transfer();
                case 5 -> viewAccounts();
                case 6 -> viewTransactions();
                case 7 -> generateReport();
                case 8 -> {
                    saveAccounts();
                    System.out.println(
                            "Thank you for banking with MiniBank!");
                    running = false;
                }
                default -> System.out.println(
                        "Invalid choice. Please select 1 to 8.");
            }
        }

        sc.close();
    }

    private static void showMenu() {
        System.out.println("\n========== MINIBANK MENU ==========");
        System.out.println("1. Open Account");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Money");
        System.out.println("5. View Accounts");
        System.out.println("6. View Transaction History");
        System.out.println("7. Generate End-of-Day Report");
        System.out.println("8. Save and Exit");
        System.out.println("===================================");
    }

    private static void loadAccounts() {
        try {
            if (Files.exists(ACCOUNT_FILE)) {
                Account[] saved =
                        StatePersister.load(ACCOUNT_FILE);

                for (Account account : saved) {
                    store.add(account);
                }

                Account.synchronizeAccountCounter(store.getAll());

                System.out.println(
                        saved.length + " saved account(s) loaded.");
            } else {
                // Sample accounts for the first run
                store.add(new SavingsAccount("Aarav", 1000, 500));
                store.add(new CurrentAccount("Diya", 2000, 1000));
                store.add(new FixedDepositAccount("Rohan", 5000));

                saveAccounts();

                System.out.println(
                        "Sample accounts created for your first run.");
            }

            if (Files.exists(TRANSACTION_FILE)) {
                for (String line :
                        Files.readAllLines(TRANSACTION_FILE)) {
                    store.addTransaction(line);
                }
            }

        } catch (IOException | RuntimeException e) {
            System.out.println(
                    "Could not load saved data: " + e.getMessage());
        }

        System.out.println("Accounts currently available: "
                + store.getAll().size());
    }

    private static void openAccount() {
        System.out.println("\n--- OPEN ACCOUNT ---");

        String name = readText("Enter account holder name: ");

        System.out.println("1. Savings Account");
        System.out.println("2. Current Account");
        System.out.println("3. Fixed Deposit Account");

        int type = readInt("Choose account type: ");
        long openingBalance =
                readLong("Enter opening balance (Rs.): ");

        try {
            Account account;

            switch (type) {
                case 1 -> {
                    long minimum = readLong(
                            "Enter minimum balance (Rs.): ");
                    account = new SavingsAccount(
                            name, openingBalance, minimum);
                }
                case 2 -> {
                    long overdraft = readLong(
                            "Enter overdraft limit (Rs.): ");
                    account = new CurrentAccount(
                            name, openingBalance, overdraft);
                }
                case 3 -> {
                    account = new FixedDepositAccount(
                            name, openingBalance);
                }
                default -> {
                    System.out.println("Invalid account type.");
                    return;
                }
            }

            store.add(account);
            saveAccounts();

            System.out.println("Account created successfully!");
            System.out.println(account);

        } catch (IllegalArgumentException e) {
            System.out.println(
                    "Account creation failed: " + e.getMessage());
        }
    }

    private static void deposit() {
        System.out.println("\n--- DEPOSIT MONEY ---");

        Account account = findAccountByInput();
        if (account == null) {
            return;
        }

        long amount = readLong("Enter deposit amount (Rs.): ");

        try {
            account.deposit(amount);

            recordTransaction(
                    "DEPOSIT",
                    account,
                    amount);

            saveAccounts();

            System.out.println("Deposit successful!");
            System.out.println("Updated balance: Rs. "
                    + account.getBalance());

        } catch (InvalidAmountException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }
    }

    private static void withdraw() {
        System.out.println("\n--- WITHDRAW MONEY ---");

        Account account = findAccountByInput();
        if (account == null) {
            return;
        }

        long amount = readLong("Enter withdrawal amount (Rs.): ");

        try {
            account.withdraw(amount);

            recordTransaction(
                    "WITHDRAWAL",
                    account,
                    amount);

            saveAccounts();

            System.out.println("Withdrawal successful!");
            System.out.println("Remaining balance: Rs. "
                    + account.getBalance());

        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
            System.out.println("Shortfall: Rs. " + e.getShortfall());
        } catch (InvalidAmountException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
        }
    }

    private static void transfer() {
        System.out.println("\n--- TRANSFER MONEY ---");

        System.out.println("Source account:");
        Account source = findAccountByInput();
        if (source == null) {
            return;
        }

        System.out.println("Destination account:");
        Account destination = findAccountByInput();
        if (destination == null) {
            return;
        }

        if (source == destination) {
            System.out.println(
                    "Source and destination must be different accounts.");
            return;
        }

        long amount = readLong("Enter transfer amount (Rs.): ");

        try {
            // Withdraw first; deposit only after withdrawal succeeds.
            source.withdraw(amount);

            try {
                destination.deposit(amount);
            } catch (InvalidAmountException e) {
                try {
                    source.deposit(amount);
                } catch (InvalidAmountException rollbackError) {
                    e.addSuppressed(rollbackError);
                    System.out.println(
                            "CRITICAL: Automatic rollback failed.");
                }

                System.out.println(
                        "Transfer failed: " + e.getMessage());
                return;
            }

            recordTransaction("WITHDRAWAL", source, amount);
            recordTransaction("DEPOSIT", destination, amount);

            saveAccounts();

            System.out.println("Transfer successful!");
            System.out.println("Source balance: Rs. "
                    + source.getBalance());
            System.out.println("Destination balance: Rs. "
                    + destination.getBalance());

        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }

    private static void viewAccounts() {
        System.out.println("\n--- ALL ACCOUNTS (SORTED BY BALANCE) ---");

        ArrayList<Account> accounts = store.sortedByBalance();

        if (accounts.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }

        for (Account account : accounts) {
            System.out.println(account);
        }

        System.out.println("\nTotal accounts: " + accounts.size());
    }

    private static void viewTransactions() {
        System.out.println("\n--- TRANSACTION HISTORY ---");

        ArrayList<String> history =
                store.getTransactionHistory();

        if (history.isEmpty()) {
            System.out.println("No transactions recorded yet.");
            return;
        }

        for (String transaction : history) {
            System.out.println(transaction);
        }
    }

    private static void generateReport() {
        System.out.println("\n--- END-OF-DAY REPORT ---");

        try {
            ReportGenerator.generate(LOGS_FOLDER, REPORT_FILE);

            for (String line : Files.readAllLines(REPORT_FILE)) {
                System.out.println(line);
            }

            System.out.println("Report saved to: " + REPORT_FILE);

        } catch (IOException | RuntimeException e) {
            System.out.println(
                    "Could not generate report: " + e.getMessage());
        }
    }

    private static Account findAccountByInput() {
        String accountNumber =
                readText("Enter account number: ");

        Account account = store.get(accountNumber);

        if (account == null) {
            System.out.println("Account not found.");
        }

        return account;
    }

    private static void recordTransaction(
            String type, Account account, long amount) {

        String historyEntry = type
                + " | Account: " + account.getAccountNumber()
                + " | Holder: " + account.getOwnerName()
                + " | Amount: Rs. " + amount;

        store.addTransaction(historyEntry);

        // ReportGenerator expects TYPE,amount format.
        TransactionLog.append(
                TRANSACTION_FILE,
                type + "," + amount);
    }

    private static void saveAccounts() {
        try {
            Account[] accounts =
                    store.getAll().toArray(new Account[0]);

            StatePersister.save(accounts, ACCOUNT_FILE);

        } catch (RuntimeException e) {
            System.out.println(
                    "Warning: Could not save accounts: "
                            + e.getMessage());
        }
    }

    private static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Input cannot be empty.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    private static long readLong(String prompt) {
        while (true) {
            System.out.print(prompt);

            try {
                return Long.parseLong(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(
                        "Enter a valid whole number amount.");
            }
        }
    }
}
