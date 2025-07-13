package org.vendingmachine.exception;

public class ExcessCashException extends RuntimeException {

    private final int columnId;
    private final float changeAmount;

    public ExcessCashException(String message, int columnId, float changeAmount) {
        super(message);
        this.columnId = columnId;
        this.changeAmount = changeAmount;
    }

    public int getColumnId() {
        return columnId;
    }

    public float getChangeAmount() {
        return changeAmount;
    }
}
