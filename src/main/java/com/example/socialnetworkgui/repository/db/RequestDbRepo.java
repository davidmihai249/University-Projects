package com.example.socialnetworkgui.repository.db;

import com.example.socialnetworkgui.domain.FriendRequest;
import com.example.socialnetworkgui.domain.RequestStatus;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.domain.validators.RequestException;
import com.example.socialnetworkgui.domain.validators.ValidationException;
import com.example.socialnetworkgui.domain.validators.Validator;
import com.example.socialnetworkgui.repository.Repository;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class RequestDbRepo implements Repository<Tuple<Long>, FriendRequest> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<FriendRequest> validator;

    public RequestDbRepo(String url, String username, String password, Validator<FriendRequest> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public FriendRequest findOne(Tuple<Long> usersIDs) {
        if (usersIDs == null)
            throw new IllegalArgumentException("Invalid users IDs!");
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM requests WHERE sender_id = (?) AND receiver_id = (?)"))
        {
            ps.setLong(1, usersIDs.getLeft());
            ps.setLong(2, usersIDs.getRight());
            ResultSet resultSet = ps.executeQuery();
            if(resultSet.next()){
                Long senderID = resultSet.getLong("sender_id");
                Long receiverID = resultSet.getLong("receiver_id");
                int integerStatus = resultSet.getInt("status");
                RequestStatus status = getRequestStatus(integerStatus);
                if (status == null){
                    throw new ValidationException("Invalid friend request status!");
                }
                FriendRequest request = new FriendRequest(senderID,receiverID,status);
                request.setId(usersIDs);
                return request;
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
    public Iterable<FriendRequest> findAll() {
        Set<FriendRequest> requests = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM requests");
             ResultSet resultSet = ps.executeQuery())
        {
            while (resultSet.next()){
                Long senderID = resultSet.getLong("sender_id");
                Long receiverID = resultSet.getLong("receiver_id");
                int integerStatus = resultSet.getInt("status");
                RequestStatus status = getRequestStatus(integerStatus);
                if (status == null){
                    throw new ValidationException("Invalid friend request status!");
                }
                FriendRequest request = new FriendRequest(senderID,receiverID,status);
                request.setId(new Tuple<>(senderID, receiverID));
                requests.add(request);
            }
            return requests;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return requests;
    }

    @Override
    public FriendRequest save(FriendRequest request) {
        validator.validate(request);
        String sql = "INSERT INTO requests (sender_id, receiver_id, status) VALUES (?,?,?)";
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1, request.getId().getLeft());
            ps.setLong(2, request.getId().getRight());
            ps.setInt(3, getIntegerRequestStatus(request.getStatus()));
            ps.executeUpdate();
            return null;
        }
        catch (SQLException E){
            E.printStackTrace();
        }
        return request;
    }

    @Override
    public FriendRequest delete(Tuple<Long> usersIDs) {
        String sql = "DELETE FROM requests WHERE sender_id = (?) AND receiver_id = (?)";
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setLong(1, usersIDs.getLeft());
            ps.setLong(2, usersIDs.getRight());
            FriendRequest deletedFriendRequest = findOne(usersIDs);
            ps.executeUpdate();
            return deletedFriendRequest;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public FriendRequest update(FriendRequest request) {
        validator.validate(request);
        String sql = "UPDATE requests SET status = (?) WHERE sender_id = (?) AND receiver_id = (?)";
        try (Connection connection = DriverManager.getConnection(url,username,password);
             PreparedStatement ps = connection.prepareStatement(sql))
        {
            ps.setInt(1, getIntegerRequestStatus(request.getStatus()));
            ps.setLong(2, request.getId().getLeft());
            ps.setLong(3, request.getId().getRight());
            if(ps.executeUpdate() == 0){
                throw new RequestException("There is no friend request between this two users!");
            }
            return null;
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return request;
    }

    private RequestStatus getRequestStatus(int integerStatus) {
        return switch (integerStatus) {
            case -1 -> RequestStatus.REJECTED;
            case 0 -> RequestStatus.PENDING;
            case 1 -> RequestStatus.APPROVED;
            default -> null;
        };
    }

    private int getIntegerRequestStatus(RequestStatus status){
        return switch (status) {
            case REJECTED -> -1;
            case PENDING -> 0;
            case APPROVED -> 1;
        };
    }
}
