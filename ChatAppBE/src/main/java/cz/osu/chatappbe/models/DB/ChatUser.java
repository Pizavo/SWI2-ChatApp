package cz.osu.chatappbe.models.DB;

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
public class ChatUser {
	@ManyToMany
	@JoinTable(
			name = "chat_member",
			joinColumns = @JoinColumn(name = "chat_user_id"),
			inverseJoinColumns = @JoinColumn(name = "chat_room_id")
	)
	List<ChatRoom> joinedRooms = new ArrayList<>();
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private Integer id;
	@OneToMany(mappedBy = "user")
	private List<Message> messages = new ArrayList<>();
	
	@Column(nullable = false)
	private String username;
	
	@Column(nullable = false)
	private String password;
	
	public ChatUser(ChatUser chatUser) {
		this.id = chatUser.getId();
		this.joinedRooms = new ArrayList<>(chatUser.getJoinedRooms());
		this.messages = new ArrayList<>(chatUser.getMessages());
		this.username = chatUser.getUsername();
		this.password = chatUser.getPassword();
	}
	
	public void addRoom(ChatRoom chatRoom) {
		this.joinedRooms.add(chatRoom);
		chatRoom.getJoinedUsers().add(this);
	}
	
	public void addRooms(List<ChatRoom> chatRooms) {
		this.joinedRooms.addAll(chatRooms);
		chatRooms.forEach(chatRoom -> chatRoom.getJoinedUsers().add(this));
	}
	
	public void addMessage(Message message) {
		this.messages.add(message);
		message.setUser(this);
	}
}
