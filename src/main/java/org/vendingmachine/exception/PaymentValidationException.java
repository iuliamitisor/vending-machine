package org.vendingmachine.exception;

public class PaymentValidationException extends RuntimeException {
    private final int columnId;

    public PaymentValidationException(String message, int columnId) {
        super(message);
        this.columnId = columnId;
    }

    public int getColumnId() {
        return columnId;
    }
}
