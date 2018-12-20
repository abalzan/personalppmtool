package com.andrei.ppmtool.ppmtool.payload;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Username can not be blank")
    private String username;

    @NotBlank(message = "Password can not be blank")
    private String password;

}
