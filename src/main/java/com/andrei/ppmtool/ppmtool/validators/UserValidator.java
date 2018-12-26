package com.andrei.ppmtool.ppmtool.validators;

import com.andrei.ppmtool.ppmtool.model.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        User user = (User) object;

        //if user is null or empty we are alredy getting an exception if we get another exception with the same field it throw an Duplicate Key Password exception
        if (!user.getPassword().isEmpty() && user.getPassword().length() < 8) {
            errors.rejectValue("password", "Length", "Password must be at least 8 characters");
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", "Match", "Passwords must match");
        }
    }
}
