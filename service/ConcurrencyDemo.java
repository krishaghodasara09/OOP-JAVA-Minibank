package service;

import java.util.concurrent.CountDownLatch;

import exception.BankException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;

public class ConcurrencyDemo {

    public static void run() {
        System.out.println("\n===== 1. THREAD POOL =====");
        demonstrateThreadPool();

        System.out.println("\n===== 2. PRODUCER-CONSUMER =====");
        demonstrateProducerConsumer();

        System.out.println("\n===== 3. DEADLOCK DEMONSTRATION =====");
        demonstrateDeadlock();

        System.out.println("\n===== 4. DEADLOCK PREVENTION =====");
        demonstrateSafeTransfer();
    }

    private static void demonstrateThreadPool() {
        Account account =
                new CurrentAccount("Pool Account", 10000, 0);

        TransactionProcessor processor = new TransactionProcessor();

        for (int i = 1; i <= 10; i++) {
            final int taskNumber = i;

            processor.submit(() -> {
                try {
                    if (taskNumber % 2 == 0) {
                        account.deposit(100);
                        System.out.println(
                                "Task " + taskNumber + ": deposited Rs. 100");
                    } else {
                        account.withdraw(50);
                        System.out.println(
                                "Task " + taskNumber + ": withdrew Rs. 50");
                    }
                } catch (BankException e) {
                    System.out.println(
                            "Task " + taskNumber + " failed: "
                                    + e.getMessage());
                }
            });
        }

        processor.stop();

        System.out.println("Final balance: Rs. " + account.getBalance());
    }

    private static void demonstrateProducerConsumer() {
        TransactionBuffer buffer = new TransactionBuffer(3);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    buffer.put("Transaction-" + i);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                buffer.finish();
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            try {
                String transaction;

                while ((transaction = buffer.take()) != null) {
                    System.out.println("Processed: " + transaction);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Consumer");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Producer-consumer demo interrupted.");
        }
    }

    private static void demonstrateDeadlock() {
        Account a = new CurrentAccount("Deadlock A", 1000, 0);
        Account b = new CurrentAccount("Deadlock B", 1000, 0);

        CountDownLatch bothHaveFirstLock = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            synchronized (a) {
                System.out.println("Thread 1 locked Account A");
                bothHaveFirstLock.countDown();
                awaitLatch(bothHaveFirstLock);

                synchronized (b) {
                    System.out.println("Thread 1 locked Account B");
                }
            }
        }, "Transfer-A-to-B");

        Thread t2 = new Thread(() -> {
            synchronized (b) {
                System.out.println("Thread 2 locked Account B");
                bothHaveFirstLock.countDown();
                awaitLatch(bothHaveFirstLock);

                synchronized (a) {
                    System.out.println("Thread 2 locked Account A");
                }
            }
        }, "Transfer-B-to-A");

        // Daemon threads prevent the demonstration from keeping the
        // application alive after the deadlock is demonstrated.
        t1.setDaemon(true);
        t2.setDaemon(true);

        t1.start();
        t2.start();

        awaitLatch(bothHaveFirstLock);

        System.out.println(
                "Deadlock created: each thread waits for the other account.");
    }

    private static void awaitLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void transferSafely(
            Account from, Account to, long amount) throws BankException {

        if (from == null || to == null) {
            throw new BankException("Account cannot be null.");
        }

        if (from == to) {
            throw new BankException(
                    "Cannot transfer to the same account.");
        }

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Transfer amount must be greater than zero.");
        }

        Account first;
        Account second;

        // Always acquire locks in account-number order.
        if (from.getAccountNumber().compareTo(to.getAccountNumber()) < 0) {
            first = from;
            second = to;
        } else {
            first = to;
            second = from;
        }

        synchronized (first) {
            synchronized (second) {
                from.withdraw(amount);

                try {
                    to.deposit(amount);
                } catch (BankException e) {
                    // Restore the source account if the destination
                    // deposit fails.
                    from.deposit(amount);
                    throw e;
                }

                System.out.println(
                        "Safe transfer successful: Rs. " + amount);
            }
        }
    }

    private static void demonstrateSafeTransfer() {
        Account a = new CurrentAccount("Safe A", 1000, 0);
        Account b = new CurrentAccount("Safe B", 1000, 0);

        try {
            transferSafely(a, b, 100);
            transferSafely(b, a, 50);

            System.out.println(
                    "Account A balance: Rs. " + a.getBalance());
            System.out.println(
                    "Account B balance: Rs. " + b.getBalance());
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }
}
