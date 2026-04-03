package com.sidework.payment.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class SyncPaymentException extends RuntimeException {
    @ControllerAdvice
    public static class Handler {

        @ResponseStatus(HttpStatus.BAD_REQUEST)
        @ExceptionHandler(SyncPaymentException.class)
        public void handleSyncFailure() {
        }
    }
}
