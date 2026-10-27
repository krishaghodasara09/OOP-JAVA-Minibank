package service;

import model.Account;
import model.CurrentAccount;

public class RaceConditionDemo {

    private static final int THREAD_COUNT = 10;
    private static final int TIMES = 100_000;
    private static final long AMOUNT = 1;

    public static void main(String[] args) {
        long expectedBalance = (long) THREAD_COUNT * TIMES * AMOUNT;

        System.out.println("=== UNSYNCHRONIZED DEPOSITS ===");
        Account unsafeAccount = new UnsafeDemoAccount();
        runWorkers(unsafeAccount, "Unsafe-Worker-");
        System.out.println("Expected balance: " + expectedBalance);
        System.out.println("Actual balance:   " + unsafeAccount.getBalance());

        System.out.println("\n=== SYNCHRONIZED DEPOSITS ===");
        Account safeAccount = new CurrentAccount("Safe Test", 0, 0);
        runWorkers(safeAccount, "Safe-Worker-");
        System.out.println("Expected balance: " + expectedBalance);
        System.out.println("Actual balance:   " + safeAccount.getBalance());
    }

    private static void runWorkers(Account account, String prefix) {
        Thread[] threads = new Thread[THREAD_COUNT];

        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(
                new AccountWorker(account, TIMES, AMOUNT),
                prefix + (i + 1)
            );
            threads[i].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Main thread was interrupted.");
                return;
            }
        }
    }
}

