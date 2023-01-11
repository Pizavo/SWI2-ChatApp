package cz.osu.chatappbe.models.DB;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {
    @Setter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private UUID id;
    
    @ManyToMany(mappedBy = "joinedRooms")
    private List<ChatUser> joinedUsers = new ArrayList<>();
    
    @OneToMany(mappedBy = "chatRoom")
    private List<Message> messages = new ArrayList<>();
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Boolean isPublic;
    
    public ChatRoom(ChatRoom chatRoom) {
        this.id = chatRoom.getId();
        this.joinedUsers = new ArrayList<>(chatRoom.getJoinedUsers());
        this.messages = new ArrayList<>(chatRoom.getMessages());
        this.name = chatRoom.getName();
        this.isPublic = chatRoom.getIsPublic();
    }
    
    public void addMessage(Message message) {
        this.messages.add(message);
        message.setChatRoom(this);
    }
    
}
