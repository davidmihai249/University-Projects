package com.example.socialnetworkgui.repository.db;

import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.Repository;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FriendshipDbRepo implements Repository<Tuple<Long>, Friendship> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Friendship> validator;

    public FriendshipDbRepo(String url, String username, String password, Validator<Friendship> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Friendship findOne(Tuple<Long> ids) {
        if (ids == null)
            throw new IllegalArgumentException("Invalid friendship!");
        try (Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM friendships WHERE first_user_id = (?) AND second_user_id = (?)"))
        {
            ps.setLong(1, ids.getLeft());
            ps.setLong(2, ids.getRight());
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                Long userId1 = resultSet.getLong("first_user_id");
                Long userId2 = resultSet.getLong("second_user_id");
                LocalDate date = LocalDate.parse(resultSet.getDate("date").toString());
                Friendship fr = new Friendship(userId1, userId2, date);
                fr.setId(ids);
                return fr;
            }
            else{
                return null;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Friendship> findAll() {
        Set<Friendship> friendships = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM friendships");
            ResultSet resultSet = ps.executeQuery())
        {
            while (resultSet.next()){
                Long userId1 = resultSet.getLong("first_user_id");
                Long userId2 = resultSet.getLong("second_user_id");
                LocalDate date = LocalDate.parse(resultSet.getDate("date").toString());
                Friendship fr = new Friendship(userId1,userId2,date);
                fr.setId(new Tuple<>(userId1, userId2));
                friendships.add(fr);
            }
            return friendships;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return friendships;
    }

    @Override
    public Friendship save(Friendship friendship) {
        validator.validate(friendship);
        String sql = "INSERT INTO friendships (first_user_id, second_user_id, date) VALUES (?,?,?)";
        try (Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1, friendship.getId().getLeft());
            ps.setLong(2, friendship.getId().getRight());
            ps.setDate(3, Date.valueOf(friendship.getDate()));
            ps.executeUpdate();
            return null;
        }
        catch (SQLException E){
            E.printStackTrace();
        }
        return friendship;
    }

    @Override
    public Friendship delete(Tuple<Long> friendshipIds) {
        String sql = "DELETE FROM friendships WHERE first_user_id = (?) AND second_user_id = (?)";
        try (Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1, friendshipIds.getLeft());
            ps.setLong(2, friendshipIds.getRight());
            Friendship deletedFriendship = findOne(friendshipIds);
            ps.executeUpdate();
            return deletedFriendship;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Friendship update(Friendship friendship) {
        validator.validate(friendship);
        String sql = "UPDATE friendships SET date = (?) WHERE first_user_id = (?) AND second_user_id = (?)";
        try (Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setDate(1, Date.valueOf(LocalDate.now()));
            ps.setLong(2, friendship.getId().getLeft());
            ps.setLong(3, friendship.getId().getRight());
            ps.executeUpdate();
            return null;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return friendship;
    }
}
