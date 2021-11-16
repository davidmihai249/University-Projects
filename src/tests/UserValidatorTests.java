package tests;

import org.junit.jupiter.api.Test;
import domain.User;
import domain.validators.UserValidator;
import domain.validators.ValidationException;
import domain.validators.Validator;

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