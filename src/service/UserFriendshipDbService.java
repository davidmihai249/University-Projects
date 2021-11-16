package service;

import domain.Tuple;
import org.jetbrains.annotations.NotNull;
import domain.Friendship;
import domain.User;
import repository.Repository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserFriendshipDbService extends UserFriendshipService{

    public UserFriendshipDbService(Repository<Long, User> userRepo, Repository<Tuple<Long>, Friendship> friendshipRepo) {
        super(userRepo, friendshipRepo);
    }

    @Override
    public List<User> getFriends(String userFirstName, String userLastName){
        Long userId = userService.getUserID(userFirstName,userLastName);
        List<User> friends = new ArrayList<>();
        userService
                .getUserRepo()
                .findOne(userId)
                .getFriends()
                .forEach(f -> friends.add(userService.getUserRepo().findOne(f)));
        return friends;
    }

    @Override
    protected @NotNull Map<Long, List<Long>> getUserRelations() {
        Map<Long, List<Long>> userRelationships = new HashMap<>();
        userService.getUserRepo().findAll().forEach(u ->
                userRelationships.put(u.getId(), getIdsFromUsers(getFriends(u.getFirstName(),u.getLastName()))));
        return userRelationships;
    }

    private @NotNull List<Long> getIdsFromUsers(@NotNull List<User> users){
        List<Long> userIds = new ArrayList<>();
        users.forEach(u -> userIds.add(u.getId()));
        return userIds;
    }
}
