package cz.osu.chatappbe.Model.DB;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToMany
    @JoinTable(
            name = "chat_member",
            joinColumns = @JoinColumn(name = "chat_user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_room_id")
    )
    List<ChatRoom> joinedRooms = new ArrayList<>();

    @OneToMany(mappedBy = "chatUser")
    private List<Message> messages = new ArrayList<>();

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    public ChatUser(ChatUser chatUser) {
        this.userId = chatUser.getUserId();
        this.joinedRooms = new ArrayList<>(chatUser.getJoinedRooms());
        this.messages = new ArrayList<>(chatUser.getMessages());
        this.username = chatUser.getUsername();
        this.password = chatUser.getPassword();
    }

    public void addRoom(ChatRoom chatRoom) {
        this.joinedRooms.add(chatRoom);
        chatRoom.getJoinedUsers().add(this);
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        message.setChatUser(this);
    }

}
