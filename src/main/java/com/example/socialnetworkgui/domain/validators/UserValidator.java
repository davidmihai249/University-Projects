package com.example.socialnetworkgui.domain.validators;


import com.example.socialnetworkgui.domain.User;

public class UserValidator implements Validator<User> {
    /**
     * Validate an entity
     * @param entity the entity to be validated
     * @throws ValidationException if the entity is not valid
     */
    @Override
    public void validate(User entity) throws ValidationException {
        if(entity == null){
            throw new ValidationException("User entity must not be null");
        }
        if(entity.getId() == null) {
            throw new ValidationException("ID must not be null!");
        }
        if((entity.getFirstName() == null || entity.getFirstName().equals("")) && (entity.getLastName() == null || entity.getLastName().equals(""))) {
            throw new ValidationException("First name and last name must not be null!");
        }
        if(entity.getFirstName() == null || entity.getFirstName().equals("")) {
            throw new ValidationException("First name must not be null!");
        }
        if(entity.getLastName() == null || entity.getLastName().equals("")) {
            throw new ValidationException("Last name must not be null!");
        }
    }
}
