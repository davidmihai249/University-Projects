package com.example.socialnetworkgui.repository.db;

import com.example.socialnetworkgui.domain.Participant;
import com.example.socialnetworkgui.domain.Tuple;
import com.example.socialnetworkgui.repository.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipantDBRepository implements Repository<Tuple<Long>, Participant> {
    private String url;
    private String username;
    private String password;

    public ParticipantDBRepository(String url,String username,String password){
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Participant findOne(Tuple<Long> longTuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM participants WHERE \"EventID\" = " + longTuple.getLeft() +
                    " AND \"UserID\" = " +  longTuple.getRight();
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getParticipant(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Participant> findAll() {
        List<Participant> listParticipants = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM participants";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                listParticipants.add(getParticipant(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listParticipants;
    }

    public Iterable<Participant> findAll(Long idEvent) {
        List<Participant> listParticipants = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM participants WHERE \"EventID\" = " + idEvent;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                listParticipants.add(getParticipant(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listParticipants;
    }

    @Override
    public Participant save(Participant entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            int isNotified = entity.getNotified() ? 1 : 0;
            String command = "INSERT INTO participants (\"EventID\", \"UserID\", \"isNotified\") VALUES " +
                    "(" + entity.getId().getLeft() + ", " + entity.getId().getRight() + ", '" + isNotified + "') " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return getParticipant(resultSet);
                }
            } catch (SQLException e) {
                return null;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Participant delete(Tuple<Long> longTuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "DELETE FROM participants WHERE \"EventID\" = " + longTuple.getLeft() +
                    " AND \"UserID\" = " + longTuple.getRight() + " RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getParticipant(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Participant update(Participant entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            int isNotified = entity.getNotified() ? 1 : 0;
            String command = "UPDATE participants SET \"isNotified\" = '" + isNotified + "' " +
                    "WHERE \"EventID\" = " + entity.getId().getLeft() + " AND \"UserID\" = " + entity.getId().getRight() +
                    " RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getParticipant(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private Participant getParticipant(ResultSet resultSet) throws SQLException {
        Long idEvent = resultSet.getLong("EventID");
        Long idUser = resultSet.getLong("UserID");
        Boolean isNotified = resultSet.getBoolean("isNotified");
        return new Participant(idEvent, idUser, isNotified);
    }
}
