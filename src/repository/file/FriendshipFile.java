package repository.file;

import domain.Friendship;
import domain.Tuple;
import domain.validators.Validator;
import java.time.LocalDate;
import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple<Long>, Friendship>{

    public FriendshipFile(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }

    @Override
    protected Friendship extractEntity(List<String> attributes) {
        Long id1 = Long.parseLong(attributes.get(1));
        Long id2 = Long.parseLong(attributes.get(2));
        LocalDate date = LocalDate.parse(attributes.get(3));
        Friendship friendship = new Friendship(id1,id2,date);
        friendship.setId(new Tuple<>(id1, id2));
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId() + ";" + entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + entity.getDate();
    }

    @Override
    public Friendship findOne(Tuple<Long> tuple) {
        try {
            return super.findOne(tuple);
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid friendship!");
        }
    }
}
