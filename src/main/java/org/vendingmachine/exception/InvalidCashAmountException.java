package org.vendingmachine.exception;

public class InvalidCashAmountException extends RuntimeException {

    private final int columnId;

    public InvalidCashAmountException(String message, int columnId) {
        super(message);
        this.columnId = columnId;
    }

    public int getColumnId() {
        return columnId;
    }

}
