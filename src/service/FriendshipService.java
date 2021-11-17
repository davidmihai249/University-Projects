package service;

import domain.Tuple;
import org.jetbrains.annotations.Nullable;
import domain.Friendship;
import repository.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FriendshipService {
    private final Repository<Tuple<Long>, Friendship> friendshipRepo;

    /**
     * Constructor
     * @param friendshipRepo the friendships repository
     */
    public FriendshipService(Repository<Tuple<Long>, Friendship> friendshipRepo) {
        this.friendshipRepo = friendshipRepo;
    }

    /**
     * @return the friendships repository
     */
    protected Repository<Tuple<Long>, Friendship> getFriendshipRepo() {
        return friendshipRepo;
    }

    /**
     * @param userId the user's id
     * @return all the friendships ids of the user with the "userId" id
     */
    public List<Tuple<Long>> getFriendships(Long userId){
        Iterable<Friendship> allFriendships = friendshipRepo.findAll();
        List<Tuple<Long>> friendshipsId = new ArrayList<>();
        allFriendships.forEach(fr -> {
            if(fr.getId().getLeft().equals(userId) || fr.getId().getRight().equals(userId)){
                friendshipsId.add(fr.getId());
            }
        });
        return friendshipsId;
    }

    /**
     * Add a new friendship
     * @param userId1 first user's id
     * @param userId2 second user's id
     * @param date the date in which the two users became friends
     */
    public void addFriendship(Long userId1, Long userId2, LocalDate date){
        Friendship entity = new Friendship(userId1,userId2,date);
        entity.setId(new Tuple<>(userId1, userId2));
        friendshipRepo.save(entity);
    }

    /**
     * Remove a friendship by id
     * @param friendshipId the id of the friendship wanted to be removed
     */
    public void removeFriendship(Tuple<Long> friendshipId){
        friendshipRepo.delete(friendshipId);
    }

    /**
     * @param userId1 id of the first user
     * @param userId2 id of the second user
     * @return the id of the friendship between user1 and user2 or null if there is no friendship between the two users
     */
    public @Nullable Tuple<Long> getFriendshipId(Long userId1, Long userId2){
        Iterable<Friendship> friendships = friendshipRepo.findAll();
        for(Friendship fr : friendships){
            if (fr.getId().getLeft().equals(userId1) && fr.getId().getRight().equals(userId2) ||
                    fr.getId().getLeft().equals(userId2) && fr.getId().getRight().equals(userId1)) {
                return fr.getId();
            }
        }
        return null;
    }
}
