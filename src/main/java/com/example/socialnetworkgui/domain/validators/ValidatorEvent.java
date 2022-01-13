package com.example.socialnetworkgui.domain.validators;

import com.example.socialnetworkgui.domain.Event;

public class ValidatorEvent implements Validator<Event>{
    @Override
    public void validate(Event entity) throws ValidationException{
        String errors = "";
        if(entity.getName().matches("[ ]*")){
            errors += "Empty name.\n";
        }
        if(entity.getStartDate()==null){
            errors += "Empty Start Date.\n";
        }
        if(entity.getEndDate()==null){
            errors += "Empty End Date.\n";
        }
        if(entity.getLocation().matches("[ ]*")){
            errors += "Empty location.\n";
        }
        if(entity.getCategory().matches("[ ]*")){
            errors += "Empty category.\n";
        }
        if(entity.getDescription().matches("[ ]*")){
            errors += "Empty description.\n";
        }
        if(errors.length()>0){
            throw new ValidationException("Empty fields!");
        }
    }
}
