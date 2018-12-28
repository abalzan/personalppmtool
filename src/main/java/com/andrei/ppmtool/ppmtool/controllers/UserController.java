package com.andrei.ppmtool.ppmtool.controllers;

import com.andrei.ppmtool.ppmtool.model.User;
import com.andrei.ppmtool.ppmtool.payload.JWTLoginSuccessResponse;
import com.andrei.ppmtool.ppmtool.payload.LoginRequest;
import com.andrei.ppmtool.ppmtool.security.JWTTokenProvider;
import com.andrei.ppmtool.ppmtool.security.SecurityConstants;
import com.andrei.ppmtool.ppmtool.services.UserService;
import com.andrei.ppmtool.ppmtool.services.ValidationErrorService;
import com.andrei.ppmtool.ppmtool.validators.UserValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final UserValidator userValidator;

    private final JWTTokenProvider jwtTokenProvider;

    private final AuthenticationManager authenticationManager;

    public UserController(ValidationErrorService validationErrorService, UserService userService, UserValidator userValidator, JWTTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.validationErrorService = validationErrorService;
        this.userService = userService;
        this.userValidator = userValidator;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, BindingResult result) {

        ResponseEntity<?> errorMap = validationErrorService.validationErrorService(result);
        if (errorMap != null) return errorMap;

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + jwtTokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JWTLoginSuccessResponse(true, jwt));

    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult result) {

        userValidator.validate(user, result);

        ResponseEntity<?> errorMap = validationErrorService.validationErrorService(result);
        if (errorMap != null) return errorMap;

        User newUser = userService.saveUser(user);

        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
