package com.example.socialnetworkgui.domain.validators;

import com.example.socialnetworkgui.domain.FriendRequest;

public class RequestValidator implements Validator<FriendRequest> {
    @Override
    public void validate(FriendRequest entity) throws ValidationException {
        if(entity == null){
            throw new ValidationException("Request entity must not be null!");
        }
        if(entity.getId() == null) {
            throw new ValidationException("ID must not be null!");
        }
        if(entity.getId().getLeft() == null) {
            throw new ValidationException("UserId1 must not be null!");
        }
        if(entity.getId().getRight() == null) {
            throw new ValidationException("UserId2 must not be null!");
        }
        if(entity.getStatus() == null){
            throw new ValidationException("Status must not be null!");
        }
    }
}
