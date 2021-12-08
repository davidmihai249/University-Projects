package com.example.socialnetworkgui.tests;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.UserValidator;
import com.example.socialnetworkgui.domain.validators.ValidationException;
import com.example.socialnetworkgui.domain.validators.Validator;
import org.junit.jupiter.api.Test;

class UserValidatorTests {
    @Test
    void validation() {
        Validator<User> validator = new UserValidator();
        User user = new User("bogdan","bentea");
        user.setId(1L);
        try {
            validator.validate(user);
            assert true;
        }
        catch (Exception e){
            assert false;
        }
        user.setFirstName(null);
        try {
            validator.validate(user);
        }
        catch (ValidationException e){
            assert true;
        }
        user.setLastName(null);
        try {
            validator.validate(user);
        }
        catch (ValidationException e){
            assert true;
        }
    }
}