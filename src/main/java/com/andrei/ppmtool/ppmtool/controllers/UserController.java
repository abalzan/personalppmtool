package com.andrei.ppmtool.ppmtool.controllers;

import com.andrei.ppmtool.ppmtool.model.User;
import com.andrei.ppmtool.ppmtool.services.UserService;
import com.andrei.ppmtool.ppmtool.services.ValidationErrorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ValidationErrorService validationErrorService;

    private final UserService userService;

    public UserController(ValidationErrorService validationErrorService, UserService userService) {
        this.validationErrorService = validationErrorService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {

        ResponseEntity<?> errorMap = validationErrorService.validationErrorService(result);
        if (errorMap != null) return errorMap;

        User newUser = userService.saveUser(user);

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
