package repository.memory;

import domain.Entity;
import domain.validators.Validator;
import repository.Repository;
import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private final Validator<E> validator;
    Map<ID,E> entities;

    /**
     * Constructor
     * @param validator the validator which validates the entities of type E
     */
    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities = new HashMap<>();
    }

    /**
     * @param id the id of the entity to be returned
     *           id must not be null
     * @return the entity that has the specified id
     * @throws IllegalArgumentException if the id is null
     */
    @Override
    public E findOne(ID id){
        if (id == null)
            throw new IllegalArgumentException("id must be not null");
        return entities.get(id);
    }

    /**
     * @return all the values of the entities
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    /**
     * @param entity must be not null
     * @return null if the entity was saved, or the entity if it wasn't saved
     * @throws IllegalArgumentException if the entity is null
     */
    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("Entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return entity;
        }
        else entities.put(entity.getId(),entity);
        return null;
    }

    /**
     * @param id id must be not null
     * @return the deleted entity
     * @throws IllegalArgumentException if the entity doesn't exist
     */
    @Override
    public E delete(ID id) {
        E entity = entities.get(id);
        if(entity == null)
            throw new IllegalArgumentException("This entity does not exist!");
        entities.remove(id);
        return entity;
    }

    /**
     * @param entity entity must not be null
     * @return null if the entity was updated, otherwise return the entity
     * @throws IllegalArgumentException if the entity is null
     */
    @Override
    public E update(E entity) {
        if(entity == null)
            throw new IllegalArgumentException("This entity does not exist!");
        validator.validate(entity);
        entities.put(entity.getId(),entity);
        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;
    }
}
