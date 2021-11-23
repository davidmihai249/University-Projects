package repository.db;

import domain.Message;
import domain.User;
import domain.validators.MessageException;
import domain.validators.Validator;
import repository.Repository;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageDBRepo implements Repository<Long, Message> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Message> validator;
    private final Repository<Long, User> userRepo;

    public MessageDBRepo(String url, String username, String password, Validator<Message> validator, Repository<Long, User> userRepo) {
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
                Long replyID = resultSet.getLong("reply_id");
                if(replyID == 0){
                    replyID = null;
                }
                List<User> toUsers = getToUsersList(messageID);
                try{
                    Message replyMessage = findOne(replyID);
                    User senderUser = getSenderUser(messageID);
                    return new Message(senderUser,toUsers,messageText,dateTime,replyMessage);
                }
                catch (IllegalArgumentException e){
                    User senderUser = getSenderUser(messageID);
                    return new Message(senderUser,toUsers,messageText,dateTime,null);
                }
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
                Long replyID = resultSet.getLong("reply_id");
                if(replyID == 0){
                    replyID = null;
                }
                List<User> toUsers = getToUsersList(messageID);
                try{
                    Message replyMessage = findOne(replyID);
                    User senderUser = getSenderUser(messageID);
                    Message message =  new Message(senderUser,toUsers,messageText,dateTime,replyMessage);
                    messages.add(message);
                }
                catch (IllegalArgumentException e){
                        User senderUser = getSenderUser(messageID);
                        Message message = new Message(senderUser, toUsers, messageText, dateTime, null);
                        messages.add(message);
                }
            }
            return messages;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public Message save(Message entity) {
        String sql = "insert into messages (message,date,reply_id) values (?,?,?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1,entity.getMessage());
            ps.setTimestamp(2,Timestamp.valueOf(entity.getDate()));
            if(entity.getReply()!=null) {
                ps.setLong(3, entity.getReply().getId());
            }
            else{
                ps.setLong(3,0);
            }
            ps.executeUpdate();
            Long id;
            if(entity.getReply()!=null) {
                id = search_id(entity.getMessage(), entity.getDate(), entity.getReply().getId());
            }
            else {
                id = search_id(entity.getMessage(), entity.getDate(), 0L);
            }
            entity.setId(id);
            insert_corr(entity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Long search_id(String message,LocalDateTime date,Long reply_id){
        Long id = null;
        try(Connection connection1 = DriverManager.getConnection(url,username,password);
            PreparedStatement ps2 = connection1.prepareStatement("SELECT id FROM messages WHERE message = (?) AND date = (?) AND reply_id = (?)")){
            ps2.setString(1,message);
            ps2.setTimestamp(2,Timestamp.valueOf(date));
            ps2.setLong(3,reply_id);
            ResultSet resultSet = ps2.executeQuery();
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
        String sql2 = "insert into correspondences (sender_id,receiver_id,message_id) values (?,?,?)";
        try(Connection connection1 = DriverManager.getConnection(url,username,password);
            PreparedStatement ps2 = connection1.prepareStatement(sql2)){

            for(User user:entity.getToUser()){
                ps2.setLong(1,entity.getFromUser().getId());
                ps2.setLong(2,user.getId());
                ps2.setLong(3,entity.getId());
                ps2.executeUpdate();
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
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
