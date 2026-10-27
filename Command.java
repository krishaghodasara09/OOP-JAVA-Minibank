enum TransactionType {
    DEPOSIT, WITHDRAW, TRANSFER
}

record Command(
        TransactionType type,
        String accountNumber,
        long amount
) {}

