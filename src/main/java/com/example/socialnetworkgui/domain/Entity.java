package com.example.socialnetworkgui.domain;

import java.io.Serial;
import java.io.Serializable;

public class Entity<ID> implements Serializable {

    @Serial
    private static final long serialVersionUID = 7331115341259248461L;

    private ID id;

    /**
     * @return the entity's id
     */
    public ID getId() {
        return id;
    }

    /**
     * Modify the id of the entity
     * @param id entity's new id
     */
    public void setId(ID id) {
        this.id = id;
    }
}