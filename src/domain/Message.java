package domain;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Long>{
    private User fromUser;
    private List<User> toUser;
    private String message;
    private LocalDateTime date;
    private Message reply;

    public Message(User fromUser, List<User> toUser, String message, LocalDateTime date,Message reply){
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = message;
        this.date = date;
        this.reply = reply;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public List<User> getToUser() {
        return toUser;
    }

    public void setToUser(List<User> toUser) {
        this.toUser = toUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Message getReply() {
        return reply;
    }

    public void setReply(Message reply) {
        this.reply = reply;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(fromUser, message1.fromUser) && Objects.equals(toUser, message1.toUser) && Objects.equals(message, message1.message) && Objects.equals(date, message1.date) && Objects.equals(reply, message1.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromUser, toUser, message, date, reply);
    }

    @Override
    public String toString() {
        return "Message{" +
                "fromUser=" + fromUser +
                ", toUser=" + toUser +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", reply=" + reply +
                '}';
    }
}
