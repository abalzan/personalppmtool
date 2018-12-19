package com.andrei.ppmtool.ppmtool.exceptions;

public class InvalidLoggingResponse {

    private String username;
    private String password;

    public InvalidLoggingResponse() {
        this.username = "Invalid Username";
        this.password = "Invalid Password";
    }
}
