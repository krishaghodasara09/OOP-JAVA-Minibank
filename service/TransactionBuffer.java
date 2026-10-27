package service;

public class TransactionBuffer {

    private final String[] buffer;
    private int count = 0;
    private int in = 0;
    private int out = 0;
    private boolean finished = false;

    public TransactionBuffer(int capacity) {
        buffer = new String[capacity];
    }

    public synchronized void put(String transaction)
            throws InterruptedException {

        while (count == buffer.length) {
            wait();
        }

        buffer[in] = transaction;
        in = (in + 1) % buffer.length;
        count++;

        System.out.println("Produced: " + transaction);

        notifyAll();
    }

    public synchronized String take() throws InterruptedException {

        while (count == 0 && !finished) {
            wait();
        }

        if (count == 0 && finished) {
            return null;
        }

        String transaction = buffer[out];
        buffer[out] = null;
        out = (out + 1) % buffer.length;
        count--;

        notifyAll();

        return transaction;
    }

    public synchronized void finish() {
        finished = true;
        notifyAll();
    }
}

