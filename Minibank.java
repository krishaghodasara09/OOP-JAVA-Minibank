
import java.util.Scanner;

public class MiniBank {

    record BankInfo(String name, String branch) {}

    enum MenuOption {
        OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT
    }

    public static void main(String[] args) {

        // Application header
        BankInfo bank = new BankInfo("MiniBank", "Main Branch");
        System.out.println("Welcome to " + bank.name()
                + " - " + bank.branch());

        // Practical 1: Menu
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
                    "Open Account - to be implemented in a later lab";
                case DEPOSIT ->
                    "Deposit - to be implemented in a later lab";
                case WITHDRAW ->
                    "Withdraw - to be implemented in a later lab";
                case TRANSFER ->
                    "Transfer - to be implemented in a later lab";
                case EXIT ->
                    "Goodbye! Thank you for using MiniBank.";
            };

            System.out.println(message);

        } while (choice != MenuOption.EXIT);

        // Practical 2: Create and test accounts
        Account[] accounts = {
            new Account("Aarav", 1000),
            new Account("Diya", 2000),
            new Account("Rohan")
        };

        accounts[0].deposit(500);
        accounts[1].withdraw(300);
        accounts[2].deposit(1000);

        System.out.println("\n===== ACCOUNT BALANCES =====");
        for (Account account : accounts) {
            System.out.println(account.getOwnerName()
                    + ": Rs. " + account.getBalance());
        }

        // Practical 3: toString()
        System.out.println("\n===== ACCOUNT DETAILS =====");
        for (Account account : accounts) {
            System.out.println(account.toString());
        }

        // Practical 3: equals()
        Account anotherAccount = new Account("Aarav", 5000);
        System.out.println("\nAre accounts equal? "
                + accounts[0].equals(anotherAccount));

        // Practical 3: instanceof
        Object obj = accounts[0];
        System.out.println("Is object an Account? "
                + (obj instanceof Account));

        // Practical 3: Customer clone and Address
        Customer customer = new Customer(
                "Aarav", "aarav@example.com", "9876543210");

        customer.setAddress(new Customer.Address(
                "12 Main Road", "Ahmedabad", "380001"));

        Customer copiedCustomer = customer.clone();

        System.out.println("\nOriginal customer ID: "
                + customer.getCustomerId());
        System.out.println("Copied customer ID: "
                + copiedCustomer.getCustomerId());
        System.out.println("Customer city: "
                + customer.getAddress().getCity());

        // Practical 4: Test validators
        System.out.println("\n===== VALIDATOR TESTS =====");

        System.out.println("Valid mobile: "
                + Validator.isValidMobile("9876543210"));
        System.out.println("Invalid mobile: "
                + Validator.isValidMobile("12345"));

        System.out.println("Valid email: "
                + Validator.isValidEmail("user@example.com"));
        System.out.println("Invalid email: "
                + Validator.isValidEmail("userexample.com"));

        System.out.println("Valid PAN: "
                + Validator.isValidPan("ABCDE1234F"));
        System.out.println("Invalid PAN: "
                + Validator.isValidPan("ABC123"));

        System.out.println("Valid IFSC: "
                + Validator.isValidIfsc("SBIN0001234"));
        System.out.println("Invalid IFSC: "
                + Validator.isValidIfsc("ABC123"));

        // Practical 4: Parse a command
        System.out.println("\n===== COMMAND PARSER TEST =====");

        Command command = CommandParser.parse(
                "DEPOSIT AC0001 500");

        System.out.println("Type: " + command.type());
        System.out.println("Account Number: "
                + command.accountNumber());
        System.out.println("Amount: " + command.amount());

        // Practical 4: Format an account statement
        System.out.println("\n===== STATEMENT TEST =====");
        System.out.println(
                StatementFormatter.buildStatement(accounts[0]));

        sc.close();
    }
}
