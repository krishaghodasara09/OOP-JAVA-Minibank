import java.util.Scanner;

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

        // Practical 3: Test toString()
        System.out.println("\n===== ACCOUNT DETAILS =====");

        for (Account account : accounts) {
            System.out.println(account.toString());
        }

        // Practical 3: Test equals()
        System.out.println("\n===== EQUALITY TEST =====");

        Account anotherAccount = new Account("Aarav", 5000);

        System.out.println("Are accounts equal? "
                + accounts[0].equals(anotherAccount));

        // Practical 3: Test instanceof
        System.out.println("\n===== INSTANCEOF TEST =====");

        Object obj = accounts[0];

        if (obj instanceof Account) {
            System.out.println("The object is an Account.");
        } else {
            System.out.println("The object is not an Account.");
        }

        // Practical 3: Test Customer cloning and Address
        System.out.println("\n===== CUSTOMER CLONE TEST =====");

        Customer customer = new Customer(
                "Aarav", "aarav@example.com", "9876543210");

        customer.setAddress(new Customer.Address(
                "12 Main Road", "Ahmedabad", "380001"));

        Customer copiedCustomer = customer.clone();

        System.out.println("Original customer ID: "
                + customer.getCustomerId());

        System.out.println("Copied customer ID: "
                + copiedCustomer.getCustomerId());

        System.out.println("Address city: "
                + customer.getAddress().getCity());

        sc.close();
    }
}
