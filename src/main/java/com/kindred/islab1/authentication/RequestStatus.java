package com.kindred.islab1.authentication;

import lombok.Getter;

@Getter
public enum RequestStatus {
    SEND,
    UNDER_CONSIDERATION,
    APPROVED,
    DENIED
}