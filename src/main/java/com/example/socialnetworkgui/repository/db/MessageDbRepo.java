package com.example.socialnetworkgui.repository.db;

import com.example.socialnetworkgui.domain.Message;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.MessageException;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.Repository;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageDbRepo implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;
    private final Repository<Long, User> userRepo;

    public MessageDbRepo(String url, String username, String password, Validator<Message> validator, Repository<Long, User> userRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        this.userRepo = userRepo;
    }

    @Override
    public Message findOne(Long messageID) {
        if (messageID == null)
            throw new IllegalArgumentException("Invalid message ID!");
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM messages WHERE id = (?)"))
        {
            ps.setLong(1, messageID);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                String messageText = resultSet.getString("message");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                Message message = new Message(getSenderUser(messageID),getToUsersList(messageID),messageText,dateTime,null); //todo
                message.setId(messageID);
                return message;
            }
            else{
                return null;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private User getSenderUser(Long messageID){
        if(messageID == null){
            throw new MessageException("Invalid message id!");
        }
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement("SELECT sender_id FROM correspondences WHERE message_id = (?)"))
        {
            ps.setLong(1, messageID);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                Long userID = resultSet.getLong("sender_id");
                return userRepo.findOne(userID);
            }
            return null;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    private List<User> getToUsersList(Long messageID){
        if(messageID == null){
            throw new MessageException("Invalid message id!");
        }
        List<User> toUsers = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement("SELECT receiver_id FROM correspondences WHERE message_id = (?)"))
        {
            ps.setLong(1, messageID);
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                Long userID = resultSet.getLong("receiver_id");
                User user = userRepo.findOne(userID);
                toUsers.add(user);
            }
            return toUsers;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return toUsers;
    }

    public Long findReply(Long ID){
        if(ID==null){
            throw new MessageException("Invalid message id!");
        }
        else{
            try (Connection connection = DriverManager.getConnection(url,username,password);
                 PreparedStatement ps = connection.prepareStatement("SELECT reply_id FROM correspondences WHERE message_id = (?)"))
            {
                ps.setLong(1, ID);
                ResultSet resultSet = ps.executeQuery();
                if(resultSet.next()){
                    return resultSet.getLong("reply_id");
                }
            }
            catch (SQLException e){
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public Iterable<Message> findAll() {
        Set<Message> messages = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM messages"))
        {
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                Long messageID = resultSet.getLong("id");
                String messageText = resultSet.getString("message");
                LocalDateTime dateTime = resultSet.getTimestamp("date").toLocalDateTime();
                List<User> toUsers = getToUsersList(messageID);
                Long replyID = findReply(messageID);
                Message reply = findOne(replyID);
                Message message = new Message(getSenderUser(messageID),toUsers,messageText,dateTime,reply);
                message.setId(messageID);
                messages.add(message);

            }
            return messages;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message save(Message entity) {
        validateMessage(entity);

        String sql = "insert into messages (message,date) values (?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1,entity.getMessage());
            ps.setTimestamp(2,Timestamp.valueOf(entity.getDate()));
            ps.executeUpdate();
            Long id = search_id(entity.getMessage(), entity.getDate());
            entity.setId(id);
            insert_corr(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set a valid id to the message and then validate the message
     * @param entity message to be validated
     */
    private void validateMessage(Message entity){
        entity.setId(0L);
        validator.validate(entity);
        entity.setId(null);
    }

    /**
     * @param message content of a message
     * @param date the date when the message was sent
     * @return the id of the Message that has the content "message", and has been sent in "date"
     */
    private Long search_id(String message,LocalDateTime date){
        Long id = null;
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement("SELECT id FROM messages WHERE message = (?) AND date = (?)")){
            ps.setString(1,message);
            ps.setTimestamp(2,Timestamp.valueOf(date));
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                id = resultSet.getLong("id");
            }
            return id;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    private void insert_corr(Message entity){
        String sql = "insert into correspondences (sender_id,receiver_id,message_id,reply_id) values (?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql)){

            for(User user : entity.getToUser()){
                ps.setLong(1,entity.getFromUser().getId());
                ps.setLong(2,user.getId());
                ps.setLong(3,entity.getId());
                if(entity.getReply() == null){
                    ps.setLong(4,0L);
                }
                else{
                    ps.setLong(4,entity.getReply().getId());
                }
                ps.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Message> getReceivedMessagesIDs(Long userID){
        List<Message> messages = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement("SELECT message_id, reply_id FROM correspondences WHERE receiver_id = (?)"))
        {
            ps.setLong(1, userID);
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                Long messageID = resultSet.getLong("message_id");
                Message message = findOne(messageID); //todo
                message.setReply(findOne(findReply(messageID)));
                messages.add(message);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message delete(Long aLong) {
        return null;
    }

    @Override
    public Message update(Message entity) {
        return null;
    }
}
