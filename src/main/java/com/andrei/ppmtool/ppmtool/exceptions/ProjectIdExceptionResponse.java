package com.andrei.ppmtool.ppmtool.exceptions;

import lombok.Getter;

@Getter
public class ProjectIdExceptionResponse {

    private final String projectIdentifier;

    public ProjectIdExceptionResponse(String projectIdentifier) {
        this.projectIdentifier = projectIdentifier;
    }
}
