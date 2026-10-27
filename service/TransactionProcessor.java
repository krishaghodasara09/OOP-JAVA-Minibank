package service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionProcessor {

    private final ExecutorService executor =
            Executors.newFixedThreadPool(4);

    public void submit(Runnable task) {
        executor.execute(task);
    }

    public void stop() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();

                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    System.out.println(
                            "Some transaction tasks did not finish.");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("Waiting for transactions was interrupted.");
        }
    }
}

