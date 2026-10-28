import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.Account;
import model.Command;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import model.WithdrawRule;
import service.BankSession;
import service.CommandParser;
import service.ConcurrencyDemo;
import util.AnnotationValidator;
import util.ReportGenerator;
import util.StatePersister;
import util.StatementFormatter;
import util.TransactionLog;
import util.Validator;

import static util.Validator.isValidMobile;

public class MiniBank {

    record BankInfo(String name, String branch) {}

    enum MenuOption {
        OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT
    }

    public static void main(String[] args) {
        BankInfo bank = new BankInfo("MiniBank", "Main Branch");
        System.out.println("Welcome to " + bank.name()
                + " - " + bank.branch());

        Scanner sc = new Scanner(System.in);
        MenuOption choice = null;

        do {
            System.out.println("\n===== MINIBANK MENU =====");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int number = sc.nextInt();

            if (number < 1 || number > 5) {
                System.out.println("Invalid choice. Try again.");
                continue;
            }

            choice = MenuOption.values()[number - 1];

            String message = switch (choice) {
                case OPEN_ACCOUNT ->
                        "Open Account - to be implemented later";
                case DEPOSIT ->
                        "Deposit - to be implemented later";
                case WITHDRAW ->
                        "Withdraw - to be implemented later";
                case TRANSFER ->
                        "Transfer - to be implemented later";
                case EXIT -> "Goodbye!";
            };

            System.out.println(message);

        } while (choice != MenuOption.EXIT);

        Account[] accounts = {
            new SavingsAccount("Aarav", 1000, 500),
            new CurrentAccount("Diya", 2000, 1000),
            new FixedDepositAccount("Rohan", 5000)
        };

        
        // ===== PRACTICAL: GENERIC REPOSITORY =====
        System.out.println("\n===== GENERIC REPOSITORY =====");

        util.Repository<Account> repository =
                new util.Repository<>();

        // Save all accounts
        repository.saveAll(accounts);

        // Fetch one account using its account number
        String searchId = accounts[0].getAccountNumber();

        System.out.println("\nAccount found by ID:");
        System.out.println(repository.findById(searchId));

        // Print all stored accounts
        System.out.println("\nAll accounts in repository:");
        for (Account account : repository.findAll()) {
            System.out.println(account);
        }

        // Demonstrate deletion
        System.out.println("\nAccount deleted: "
                + repository.delete("INVALID-ID"));

        // Demonstrate the bounded generic method
        Integer[] numbers = {10, 20, 30, 40};
        System.out.println("\nAverage: "
                + util.Repository.average(numbers));

        // ===== MILESTONE 2: COLLECTIONS FRAMEWORK =====
        System.out.println("\n===== MILESTONE 2: ACCOUNT STORE =====");

        util.AccountStore store = new util.AccountStore();

        for (Account account : accounts) {
            store.add(account);
        }

        System.out.println("\nAccounts sorted by balance:");

        for (Account account : store.sortedByBalance()) {
            System.out.println(account);
        }

        // Demonstrate account lookup
        if (accounts.length > 0) {
            String searchNumber = accounts[0].getAccountNumber();

            System.out.println("\nSearching for account: " + searchNumber);
            System.out.println(store.get(searchNumber));
        }

        // Demonstrate transaction history
        store.addTransaction("DEPOSIT,100");
        store.addTransaction("WITHDRAWAL,50");

        System.out.println("\nTransaction history:");
        for (String transaction : store.getTransactionHistory()) {
            System.out.println(transaction);
        }

        System.out.println("\n===== ACCOUNT INTEREST =====");

        for (Account account : accounts) {
            System.out.println(account);
            System.out.println("Rate: "
                    + account.interestRate() + "%");
            System.out.println("Yearly interest: Rs. "
                    + account.yearlyInterest());
        }

        WithdrawRule rule1 = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.canWithdraw(amount);
            }
        };

        System.out.println("\nAnonymous class allows withdrawal: "
                + rule1.allow(accounts[0], 200));

        WithdrawRule rule2 =
                (account, amount) -> account.canWithdraw(amount);

        System.out.println("Lambda allows withdrawal: "
                + rule2.allow(accounts[0], 200));

        System.out.println("\nValid mobile: "
                + isValidMobile("9876543210"));
        System.out.println("Valid email: "
                + Validator.isValidEmail("user@example.com"));

        Command command =
                CommandParser.parse("DEPOSIT AC0001 500");

        System.out.println("\nParsed command: "
                + command.type() + ", "
                + command.accountNumber() + ", "
                + command.amount());

        System.out.println("\n"
                + StatementFormatter.buildStatement(accounts[0]));

        // Practical 8: Checked exceptions.
        System.out.println("\n===== EXCEPTION HANDLING =====");

        try {
            accounts[0].withdraw(700);
        } catch (InsufficientFundsException e) {
            System.out.println("Withdrawal failed: " + e.getMessage());
            System.out.println("Shortfall: Rs. " + e.getShortfall());
        } catch (InvalidAmountException e) {
            System.out.println("Invalid withdrawal: " + e.getMessage());
        } finally {
            System.out.println("Withdrawal attempt completed.");
        }

        try {
            accounts[0].deposit(-100);
        } catch (InvalidAmountException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }

        try {
            accounts[0].transfer(accounts[1], 200);
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        try {
            accounts[0].transfer(null, 100);
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }

        // Practical 8: Try-with-resources.
        System.out.println("\n===== TRY-WITH-RESOURCES =====");

        try (BankSession session = new BankSession()) {
            System.out.println("Performing banking operations...");
        } finally {
            System.out.println("Session block finished.");
        }

        // Practical 7: Annotation and reflection validation.
        System.out.println("\n===== ANNOTATION VALIDATION =====");

        Account testAccount =
                new SavingsAccount("Test User", 1000, 500);

        try {
            Field balanceField =
                    Account.class.getDeclaredField("balance");

            balanceField.setAccessible(true);
            balanceField.setLong(testAccount, -100);

            String[] errors =
                    AnnotationValidator.validate(testAccount);

            if (errors.length == 0) {
                System.out.println("No validation errors.");
            } else {
                for (String error : errors) {
                    System.out.println("Validation error: " + error);
                }
            }
        } catch (ReflectiveOperationException e) {
            System.out.println("Validation test failed: "
                    + e.getMessage());
        }

        // Practical 10: Concurrency demonstrations.
        ConcurrencyDemo.run();

        // Practical 11: Serialization, transaction logging and reporting.
        demonstratePersistenceAndReporting(accounts);

        sc.close();
    }

    private static void demonstratePersistenceAndReporting(
            Account[] accounts) {

        Path accountFile = Path.of("data", "accounts.dat");
        Path logsFolder = Path.of("logs");
        Path transactionFile = logsFolder.resolve("transactions.log");
        Path reportFile = Path.of(
                "reports", "end-of-day-report.txt");

        System.out.println("\n===== PRACTICAL 11: PERSISTENCE =====");

        try {
            // Save the accounts to a file.
            StatePersister.save(accounts, accountFile);
            System.out.println("Saved to: " + accountFile);

            // Load into a separate array.
            Account[] loadedAccounts =
                    StatePersister.load(accountFile);

            System.out.println("\nReloaded accounts:");

            for (Account account : loadedAccounts) {
                System.out.println(account);
            }

            // Append sample transactions to the log.
            TransactionLog.append(transactionFile, "DEPOSIT,100");
            TransactionLog.append(transactionFile, "WITHDRAWAL,50");
            TransactionLog.append(transactionFile, "DEPOSIT,200");

            System.out.println("\nTransactions appended to: "
                    + transactionFile);

            // Generate the end-of-day report.
            ReportGenerator.generate(logsFolder, reportFile);

            System.out.println("\n===== END-OF-DAY REPORT =====");

            for (String line : Files.readAllLines(reportFile)) {
                System.out.println(line);
            }

            System.out.println("\nReport saved to: " + reportFile);

        } catch (IOException e) {
            System.out.println("File operation failed: "
                    + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Persistence operation failed: "
                    + e.getMessage());
        }
    }
}
