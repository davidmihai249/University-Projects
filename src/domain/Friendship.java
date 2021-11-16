package domain;

import java.time.LocalDate;

public class Friendship extends Entity<Tuple<Long>>{
    LocalDate date;

    /**
     * Constructor
     * @param id1 first user's id
     * @param id2 second user's id
     * @param date the date in which the users became friends
     */
    public Friendship(Long id1, Long id2, LocalDate date) {
        setId(new Tuple<>(id1, id2));
        this.date = date;
    }

    @Override
    public void setId(Tuple<Long> longTuple) {
        super.setId(longTuple);
    }

    /**
     * @return the date in which the two users became friends
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date the new date to modify the current friendship's date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }
}
