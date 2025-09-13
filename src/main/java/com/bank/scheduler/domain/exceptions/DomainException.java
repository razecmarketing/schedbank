package com.bank.scheduler.domain.exceptions;

public class DomainException extends RuntimeException {
    private final String code;

    public DomainException(String message, String code) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static class InvalidTransferAmount extends DomainException {
        public InvalidTransferAmount(String message) {
            super(message, "TRANSFER.INVALID_AMOUNT");
        }
    }

    public static class InvalidAccountNumber extends DomainException {
        public InvalidAccountNumber(String message) {
            super(message, "ACCOUNT.INVALID_NUMBER");
        }
    }

    public static class InvalidTransferDate extends DomainException {
        public InvalidTransferDate(String message) {
            super(message, "TRANSFER.INVALID_DATE");
        }
    }

    public static class NoApplicableFeePolicy extends DomainException {
        public NoApplicableFeePolicy(String message) {
            super(message, "FEE.NO_APPLICABLE_POLICY");
        }
    }

    public static class SameAccountNotAllowed extends DomainException {
        public SameAccountNotAllowed(String message) {
            super(message, "ACCOUNT.SAME_ACCOUNT_NOT_ALLOWED");
        }
    }

    public static class InvalidTransferData extends DomainException {
        public InvalidTransferData(String message) {
            super(message, "TRANSFER.INVALID_DATA");
        }
    }

    public static class TransferNotFound extends DomainException {
        public TransferNotFound(String message) {
            super(message, "TRANSFER.NOT_FOUND");
        }
    }
}
