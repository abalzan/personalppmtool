package com.andrei.ppmtool.ppmtool.exceptions;

import lombok.Getter;

@Getter
public class UsernameAlreadyExistExceptionResponse {

    private final String username;

    public UsernameAlreadyExistExceptionResponse(String username) {
        this.username = username;
    }
}
