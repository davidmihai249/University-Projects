package domain.validators;

import domain.Message;

public class MessageValidator implements Validator<Message>{

    @Override
    public void validate(Message entity) throws ValidationException {
        if(entity == null){
            throw new ValidationException("Message entity cannot be null.");
        }
        if(entity.getId() == null){
            throw new ValidationException("Id cannot be null.");
        }
        if ((entity.getFromUser().getFirstName() == null || entity.getFromUser().getFirstName().equals("")) && (entity.getFromUser().getLastName() == null || entity.getFromUser().getLastName().equals(""))){
            throw new ValidationException("First name and last name cannot be null.");
        }
        if(entity.getFromUser().getFirstName() == null || entity.getFromUser().getFirstName().equals("")){
            throw new ValidationException("First name cannot be null.");
        }
        if(entity.getFromUser().getLastName() == null || entity.getFromUser().getLastName().equals("")){
            throw new ValidationException("Last name cannot be null.");
        }
        if(entity.getMessage() == null){
            throw new ValidationException("The message cannot be null.");
        }
        if(entity.getDate() == null){
            throw new ValidationException("The date cannot be null.");
        }
        if(entity.getToUser().isEmpty()){
            throw new ValidationException("The message has to be sent to someone.");
        }
    }
}
