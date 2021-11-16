package tests;

import domain.Tuple;
import org.junit.jupiter.api.Test;
import domain.Friendship;
import domain.validators.FriendshipValidator;
import domain.validators.ValidationException;
import domain.validators.Validator;
import java.time.LocalDate;

class FriendshipValidatorTests {
    @Test
    void validation() {
        Validator<Friendship> validator = new FriendshipValidator();
        Friendship fr = new Friendship(1L,2L, LocalDate.parse("2001-10-01"));
        fr.setId(new Tuple<>(1L,2L));
        try {
            validator.validate(fr);
            assert true;
        }
        catch (Exception e){
            assert false;
        }
//        fr.setUserId1(null);
//        try {
//            validator.validate(fr);
//        }
//        catch (ValidationException e){
//            assert true;
//        }
//        fr.setUserId2(null);
//        try {
//            validator.validate(fr);
//        }
//        catch (ValidationException e){
//            assert true;
//        }
    }
}