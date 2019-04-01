package com.leafyjava.tutorials.tccdemo.utils.enums;

public enum TccStatus {
    TRY(0), CONFIRM(1);

    private int status;

    TccStatus(final int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
