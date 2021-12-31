package com.example.socialnetworkgui.repository.db;

import com.example.socialnetworkgui.domain.Chat;
import com.example.socialnetworkgui.domain.Friendship;
import com.example.socialnetworkgui.domain.Message;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatDBRepo implements Repository<Long, Chat> {
    private final String url;
    private final String username;
    private final String password;

    public ChatDBRepo(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Chat findOne(Long aLong) {
        return null;
    }

    @Override
    public Iterable<Chat> findAll() {
        Set<Chat> chats = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM chats");
             ResultSet resultSet = ps.executeQuery())
        {
            while (resultSet.next()){
                String name = resultSet.getString("name");
                String user_ids = resultSet.getString("users_id");
                List<Long> ids = new ArrayList<>();
                for(String id:user_ids.split(" ")){
                    ids.add(Long.parseLong(id));
                }
                Chat chat = new Chat(name);
                chat.setUsers(ids);
                chats.add(chat);
            }
            return chats;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return chats;
    }

    @Override
    public Chat save(Chat entity) {
        String sql = "INSERT INTO chats (name, users_id) VALUES (?, ?)";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql) )
        {
            ps.setString(1, entity.getChatName());
            String result = null;
            for(Long id_user:entity.getUsers()){
                String id_user_string = Long.toString(id_user);
                result = result + id_user_string;
                result = result + " ";
            }
            ps.setString(2, result);
            ps.executeUpdate();
            return null;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return entity;
    }

    @Override
    public Chat delete(Long aLong) {
        return null;
    }

    @Override
    public Chat update(Chat entity) {
        return null;
    }
}
