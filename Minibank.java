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
        MenuOption choice;

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
                case EXIT -> "Goodbye! Thank you for using MiniBank.";
            };

            System.out.println(message);

        } while (choice != MenuOption.EXIT);

        // Practical 2: Test the Account class
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

        sc.close();
    }
}
