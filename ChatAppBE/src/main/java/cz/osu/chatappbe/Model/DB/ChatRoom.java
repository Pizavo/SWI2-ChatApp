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
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="chat_id", nullable = false)
    private Integer chatId;

    @ManyToMany(mappedBy = "joinedRooms")
    private List<ChatUser> joinedUsers = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom")
    private List<Message> messages = new ArrayList<>();

    @Column(name="chat_name", nullable = false)
    private String chatName;

    @Column(name="is_public")
    private Boolean isPublic;

    public ChatRoom(ChatRoom chatRoom) {
        this.chatId = chatRoom.getChatId();
        this.joinedUsers = new ArrayList<>(chatRoom.getJoinedUsers());
        this.messages = new ArrayList<>(chatRoom.getMessages());
        this.chatName = chatRoom.getChatName();
        this.isPublic = chatRoom.getIsPublic();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
        message.setChatRoom(this);
    }

}
