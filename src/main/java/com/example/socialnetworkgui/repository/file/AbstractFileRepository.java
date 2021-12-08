package com.example.socialnetworkgui.repository.file;

import com.example.socialnetworkgui.domain.Entity;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.memory.InMemoryRepository;
import java.io.*;
import java.util.Arrays;
import java.util.List;

///Aceasta clasa implementeaza sablonul de proiectare Template Method; puteti inlucui solutia propusa cu un Factory (vezi mai jos)
public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;
    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    /**
     * Load data from the file into memory
     */
    private void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while((line = br.readLine()) != null){
                if (!line.equals("")) {
                    E entity = extractEntity(Arrays.asList(line.split(";")));
                    super.save(entity);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes - the String attributes of an entity
     * @return an entity of type E
     */
    protected abstract E extractEntity(List<String> attributes);
    ///Observatie-Sugestie: in locul metodei template extractEntity, puteti avea un factory pr crearea instantelor entity

    protected abstract String createEntityAsString(E entity);

    @Override
    public E save(E entity){
        if (super.save(entity) == null){
            writeToFile(entity);
            return null;
        }
        else {
            return entity;
        }
    }

    /**
     * @param entity the entity to be appended to the file
     */
    protected void writeToFile(E entity){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))){
            writer.write(createEntityAsString(entity));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear the file, then write all entities from memory into the file
     */
    protected void writeToFile(){
        try (BufferedWriter clearer = new BufferedWriter(new FileWriter(fileName, false))){
            clearer.write("");
        }
        catch (IOException e){
            e.printStackTrace();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))){
            super.findAll().forEach(entity -> {
                try {
                    writer.write(createEntityAsString(entity));
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public E update(E entity) {
        E updatedEntity = super.update(entity);
        if (updatedEntity == null) {
            writeToFile();
            return null;
        }
        return entity;
    }

    @Override
    public E delete(ID id) {
        E deletedEntity = super.delete(id);
        if(deletedEntity != null){
            writeToFile();
            return deletedEntity;
        }
        return null;
    }
}

