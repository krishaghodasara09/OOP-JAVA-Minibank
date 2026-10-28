package model;

import java.io.Serializable;

public class SavingsAccount extends Account implements Premium, Serializable {

    private static final long serialVersionUID = 1L;

    private long minBalance;

    public SavingsAccount(String ownerName, long openingBalance,
                          long minBalance) {
        super(ownerName, openingBalance);

        if (minBalance < 0 || openingBalance < minBalance) {
            throw new IllegalArgumentException(
                    "Opening balance must be at least minimum balance.");
        }

        this.minBalance = minBalance;
    }

    @Override
    public double interestRate() {
        return 4.0;
    }

    @Override
    public boolean canWithdraw(long amount) {
        return amount > 0 && amount <= balance - minBalance;
    }

    @Override
    protected long withdrawalShortfall(long amount) {
        return Math.max(0, amount - (balance - minBalance));
    }

    public long getMinBalance() {
        return minBalance;
    }
}
