package com.kindred.islab1.authentication;

import lombok.Getter;

@Getter
public enum ImportStatus {
    SUCCESS,
    FAILED_DUE_TO_ERROR_IN_DATABASE,
    FAILED_DUE_TO_INCORRECT_DATA_IN_FILE,
    FAILED_DUE_TO_VALIDATION,
    FAILED_DUE_TO_INTERNAL_ERROR
}
