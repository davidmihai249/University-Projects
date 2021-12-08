package com.example.socialnetworkgui.tests;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

class FriendshipTests {

    Long id1;
    Long id2;
    LocalDate date;
    Friendship friendship;

    @BeforeEach
    void setUp() {
        id1 = 7L;
        id2 = 10L;
        date = LocalDate.parse("2001-10-01");
        friendship = new Friendship(id1, id2, date);
        friendship.setId(new Tuple<Long>(id1,id2));
    }

    @Test
    void getters() {
        assert (friendship.getId().equals(new Tuple<Long>(id1,id2)));
        assert (friendship.getId().getLeft().equals(7L));
        assert (friendship.getId().getRight().equals(10L));
        assert (friendship.getDate().toString().equals("2001-10-01"));
    }

    @Test
    void setters() {

    }

    @Test
    void equality() {
        Friendship fr2 = new Friendship(id1 - 1,id2 + 1,LocalDate.parse("2021-11-03"));
        assert (!friendship.equals(fr2));
        fr2.setId(new Tuple<>(id1,id2));
        fr2.setDate(LocalDate.parse("2001-10-01"));
        assert (friendship.equals(fr2));
    }
}