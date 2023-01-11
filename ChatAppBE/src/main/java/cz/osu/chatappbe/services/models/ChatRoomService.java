package cz.osu.chatappbe.services.models;

import cz.osu.chatappbe.models.DB.ChatRoom;
import cz.osu.chatappbe.models.DB.ChatUser;
import cz.osu.chatappbe.models.DB.Message;
import cz.osu.chatappbe.repositories.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatRoomService {
	@Autowired
	private ChatRoomRepository repository;
	
	public ChatRoom create(ChatUser user, ChatUser chatUser) {
		ChatRoom chatRoom = new ChatRoom();
		
		chatRoom.setName(user.getUsername() + ", " + chatUser.getUsername());
		chatRoom.setIsPublic(false);
		chatRoom.getJoinedUsers().add(user);
		chatRoom.getJoinedUsers().add(chatUser);
		
		return repository.save(chatRoom);
	}
	
	public ChatRoom create() {
		ChatRoom chatRoom = new ChatRoom();
		
		chatRoom.setName("Public");
		chatRoom.setIsPublic(true);
		
		return repository.save(chatRoom);
	}
	
	public Optional<ChatRoom> getPublicRoom() {
		return repository.findChatRoomByIsPublicIsTrue();
	}
	
	public List<ChatRoom> getUserRooms(String username) {
		List<ChatRoom> rooms = this.repository.findByJoinedUsers_Username(username);
		
		System.out.println(rooms);
		
		rooms.forEach(chatRoom -> chatRoom.setJoinedUsers(null));
		
		for (ChatRoom chatRoom : rooms) {
			for (Message message : chatRoom.getMessages()) {
				message.setChatRoom(null);
				message.getChatUser().setMessages(null);
				message.getChatUser().setJoinedRooms(null);
			}
		}
		
		return rooms;
	}
	
	public ChatRoom update(ChatRoom chatRoom) {
		return repository.save(chatRoom);
	}
	
	public Optional<ChatRoom> getGroupRoom(UUID id) {
		return repository.findById(id);
	}
}
