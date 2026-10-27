
import java.util.Scanner;

import model.Account;
import model.Command;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;
import model.WithdrawRule;
import service.CommandParser;
import util.StatementFormatter;
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

        // Menu
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

        // Polymorphism
        Account[] accounts = {
            new SavingsAccount("Aarav", 1000, 500),
            new CurrentAccount("Diya", 2000, 1000),
            new FixedDepositAccount("Rohan", 5000)
        };

        System.out.println("\n===== ACCOUNT INTEREST =====");

        for (Account account : accounts) {
            System.out.println(account);
            System.out.println("Rate: " + account.interestRate() + "%");
            System.out.println("Yearly interest: Rs. "
                    + account.yearlyInterest());
        }

        // Anonymous class implementation
        WithdrawRule rule1 = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.canWithdraw(amount);
            }
        };

        System.out.println("\nAnonymous class allows withdrawal: "
                + rule1.allow(accounts[0], 200));

        // Lambda implementation
        WithdrawRule rule2 =
                (account, amount) -> account.canWithdraw(amount);

        System.out.println("Lambda allows withdrawal: "
                + rule2.allow(accounts[0], 200));

        // Static import demonstration
        System.out.println("\nValid mobile: "
                + isValidMobile("9876543210"));
        System.out.println("Valid email: "
                + Validator.isValidEmail("user@example.com"));

        // Command parser
        Command command = CommandParser.parse("DEPOSIT AC0001 500");

        System.out.println("\nParsed command: "
                + command.type() + ", "
                + command.accountNumber() + ", "
                + command.amount());

        // Statement formatter
        System.out.println("\n"
                + StatementFormatter.buildStatement(accounts[0]));

        sc.close();
    }
}
