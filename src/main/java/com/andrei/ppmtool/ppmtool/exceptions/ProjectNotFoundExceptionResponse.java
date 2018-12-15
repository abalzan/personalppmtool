package com.andrei.ppmtool.ppmtool.exceptions;

import lombok.Getter;

@Getter
public class ProjectNotFoundExceptionResponse {

    private final String projectNotFound;

    public ProjectNotFoundExceptionResponse(String projectNotFound) {
        this.projectNotFound = projectNotFound;
    }
}
