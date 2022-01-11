package com.example.socialnetworkgui.repository.db;

import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.domain.validators.Validator;
import org.jetbrains.annotations.NotNull;
import com.example.socialnetworkgui.repository.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDbRepo implements Repository<Long, User> {
    protected final String url;
    protected final String username;
    protected final String password;
    protected final Validator<User> validator;

    public UserDbRepo(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public User findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("Invalid user!");
        User user = findUser(id);
        if (user != null){
            user.setFriends(getFriendsIds(user));
        }
        return user;
    }

    @Override
    public Iterable<User> findAll() {
        Set<User> users = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = ps.executeQuery())
        {
            while (resultSet.next()){
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(firstName,lastName);
                user.setId(id);
                users.add(user);
            }
            return users;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return users;
    }

    public void saveLogin(String first_name,String last_name,String username,String password){
        String sql = "INSERT INTO users (first_name, last_name,username,password) VALUES (?,?,?,?)";
        try(Connection connection = DriverManager.getConnection(this.url,this.username,this.password);
            PreparedStatement ps = connection.prepareStatement(sql) )
        {
            ps.setString(1, first_name);
            ps.setString(2, last_name);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public User save(User user) {
        validator.validate(user);
        String sql = "INSERT INTO users (first_name, last_name,username,password) VALUES (?, ?,?,?)";
        try(Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql) )
        {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.executeUpdate();
            return null;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return user;
    }

    /**
     * Return the user which tries to log in, or null if the credentials are invalid
     * @param usernameString the username given by the user in text field
     * @param passwordString the password given by the user in text field
     * @return an object of type User, or null
     */
    public User findUserLogin(String usernameString,String passwordString){
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username=(?) AND password=(?)"))
        {
            ps.setString(1,usernameString);
            ps.setString(2,passwordString);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                Long id = resultSet.getLong("id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(firstName,lastName);
                user.setId(id);
                return user;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public String findUserPassword(String userName){
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE username=(?)"))
        {
            ps.setString(1,userName);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                return resultSet.getString("password");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User delete(Long id) {
        if (id == null){
            throw new IllegalArgumentException("Invalid user!");
        }
        String sql = "DELETE FROM users WHERE id = (?)";
        try (Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1,id);
            User deletedUser = findOne(id);
            ps.executeUpdate();
            return deletedUser;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User update(User user) {
        validator.validate(user);
        String sql = "UPDATE users SET first_name = (?), last_name = (?) WHERE id = (?)";
        try (Connection connection = DriverManager.getConnection(url,username,password);
            PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setString(1,user.getFirstName());
            ps.setString(2,user.getLastName());
            ps.setLong(3,user.getId());
            ps.executeUpdate();
            return null;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return user;
    }

    private List<User> getFriends(@NotNull User user){
        List<User> allFriends = new ArrayList<>();
        String sql = """
                        SELECT first_user_id, second_user_id FROM friendships f
                        INNER JOIN users u ON (f.first_user_id = u.id OR f.second_user_id = u.id)
                        WHERE u.id = (?)
                        \s""";
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1, user.getId());
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()){
                long friendID;
                if (resultSet.getLong("first_user_id") == user.getId()){
                    friendID = resultSet.getLong("second_user_id");
                }
                else{
                    friendID = resultSet.getLong("first_user_id");
                }
                allFriends.add(findUser(friendID));
            }
            return allFriends;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return allFriends;
    }

    private List<Long> getFriendsIds(User user){
        List<Long> friendsId = new ArrayList<>();
        getFriends(user).forEach(u -> friendsId.add(u.getId()));
        return friendsId;
    }

    private User findUser(Long id){
        if (id == null)
            throw new IllegalArgumentException("Invalid user!");
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE id = (?)"))
        {
            ps.setLong(1, id);
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                User user = new User(firstName,lastName);
                user.setId(id);
                return user;
            }
            else{
                return null;
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
