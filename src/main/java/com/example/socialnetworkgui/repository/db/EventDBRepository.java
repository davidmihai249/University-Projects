package com.example.socialnetworkgui.repository.db;

import com.example.socialnetworkgui.domain.Event;
import com.example.socialnetworkgui.domain.User;
import com.example.socialnetworkgui.repository.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventDBRepository implements Repository<Long, Event> {
    private String url;
    private String username;
    private String password;
    private UserDbRepo userDbRepo;

    public EventDBRepository(String url, String username, String password, UserDbRepo userDbRepo){
        this.url = url;
        this.username = username;
        this.password = password;
        this.userDbRepo = userDbRepo;
    }

    @Override
    public Event findOne(Long aLong) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM events WHERE id = " + aLong;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getEvent(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public Event findOneAfterName(String Name){
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM events WHERE Name = " + Name;
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getEvent(resultSet);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Event> findAll() {
        List<Event> eventList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "SELECT * FROM events";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                eventList.add(getEvent(resultSet));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return eventList;
    }

    @Override
    public Event save(Event entity) {
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String command = "INSERT INTO events " +
                    "(\"Name\", \"startDate\", \"endDate\", \"Description\", \"Location\", \"Category\",\"OrganizerID\") VALUES " +
                    "('" + entity.getName() + "', " +
                    "'" + entity.getStartDate() + "', " +
                    "'" + entity.getEndDate() + "', " +
                    "'" + entity.getDescription() + "', " +
                    "'" + entity.getLocation() + "', " +
                    "'" + entity.getCategory() + "', " +
                    ""  + entity.getOrganizer().getId() + ") " +
                    "RETURNING *";
            PreparedStatement preparedStatement = connection.prepareStatement(command);
            try {
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return getEvent(resultSet);
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
    public Event delete(Long aLong) {
        return null;
    }

    @Override
    public Event update(Event entity) {
        return null;
    }

    private Event getEvent(ResultSet resultSet) throws SQLException {
        Long eventID = resultSet.getLong("id");
        String name = resultSet.getString("Name");
        LocalDateTime startDate = resultSet.getTimestamp("startDate").toLocalDateTime();
        LocalDateTime endDate = resultSet.getTimestamp("endDate").toLocalDateTime();
        String description = resultSet.getString("Description");
        String location = resultSet.getString("Location");
        String category = resultSet.getString("Category");
        Long organizerID = resultSet.getLong("OrganizerID");
        User organizer = userDbRepo.findOne(organizerID);
        Event event = new Event(name, startDate, endDate, description, location, category, organizer);
        event.setId(eventID);
        return event;
    }
}
