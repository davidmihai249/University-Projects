package com.example.socialnetworkgui.domain;
import com.example.socialnetworkgui.domain.Entity;
import com.example.socialnetworkgui.domain.Tuple;

public class Participant extends Entity<Tuple<Long>>{
    Boolean isNotified;

    public Participant(Long idEvent,Long idUser){
        this.isNotified = true;
        setId(new Tuple<>(idEvent,idUser));
    }

    public Participant(Long idEvent,Long idUser,Boolean isNotified){
        this.isNotified = isNotified;
        setId(new Tuple<>(idEvent,idUser));
    }

    public Boolean getNotified() {
        return isNotified;
    }

    public void setNotified(Boolean notified) {
        isNotified = notified;
    }

    @Override
    public String toString() {
        return "Participant{" +
                "idEvent" + getId().getLeft() +
                "idUser" + getId().getRight() +
                "isNotified=" + isNotified +
                '}';
    }
}
