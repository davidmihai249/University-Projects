package domain.validators;

import domain.Friendship;
import java.time.LocalDate;

public class FriendshipValidator implements Validator<Friendship>{
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity == null){
            throw new ValidationException("Friendship entity must not be null!");
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
        if(entity.getDate() == null){
            throw new ValidationException("Date must not be null!");
        }
        if(entity.getDate().compareTo(LocalDate.now()) > 0){
            throw new ValidationException("Date must not be a future date!");
        }
    }
}
