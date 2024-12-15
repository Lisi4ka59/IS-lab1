package com.kindred.islab1.exceptions;

import com.kindred.islab1.authentication.ImportStatus;
import org.springframework.http.HttpStatus;

public class ImportException extends RuntimeException {

    public ImportStatus status;
    public String user;
    public HttpStatus httpStatus;

    public ImportException(String message, ImportStatus status, String user, HttpStatus httpStatus) {
        super(message);
        this.status = status;
        this.user = user;
        this.httpStatus = httpStatus;
    }
}
