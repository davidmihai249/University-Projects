package com.example.socialnetworkgui.tests;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.validators.FriendshipValidator;
import com.example.socialnetworkgui.domain.validators.Validator;
import org.junit.jupiter.api.Test;
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