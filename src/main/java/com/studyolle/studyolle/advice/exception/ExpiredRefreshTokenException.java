package com.studyolle.studyolle.advice.exception;

public class ExpiredRefreshTokenException extends RuntimeException {

    public ExpiredRefreshTokenException(String msg) {
        super(msg);
    }

    public ExpiredRefreshTokenException() {
        super();
    }

}
