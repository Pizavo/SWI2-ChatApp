package cz.osu.chatappbe.models.DB;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom implements Serializable {
	@Setter(AccessLevel.NONE)
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private Integer id;
	
	@ManyToMany(mappedBy = "joinedRooms")
	private List<ChatUser> joinedUsers = new ArrayList<>();
	
	@OneToMany(mappedBy = "room")
	private List<Message> messages = new ArrayList<>();
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private Boolean isPublic;
	
	@Column(nullable = false)
	private Boolean isGroup;
	
	public ChatRoom(ChatRoom chatRoom) {
		this.id = chatRoom.getId();
		this.joinedUsers = new ArrayList<>(chatRoom.getJoinedUsers());
		this.messages = new ArrayList<>(chatRoom.getMessages());
		this.name = chatRoom.getName();
		this.isPublic = chatRoom.getIsPublic();
		this.isGroup = chatRoom.getIsGroup();
	}
	
	public void addMessage(Message message) {
		this.messages.add(message);
		message.setRoom(this);
	}
	
	
	public String toString() {
		return "ChatRoom{" +
		       "id=" + id +
		       ", name='" + name + '\'' +
		       ", isPublic=" + isPublic +
		       ", isGroup=" + isGroup +
		       '}';
	}
}
