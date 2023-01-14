package cz.osu.chatappbe.services.models;

import cz.osu.chatappbe.models.ChatForm;
import cz.osu.chatappbe.models.DB.ChatRoom;
import cz.osu.chatappbe.models.DB.ChatUser;
import cz.osu.chatappbe.repositories.ChatRoomRepository;
import cz.osu.chatappbe.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {
	@Autowired
	private ChatRoomRepository repository;
	@Autowired
	private UserRepository userRepository;
	
	public ChatRoom create(ChatUser user, ChatUser chatUser) {
		ChatRoom chatRoom = new ChatRoom();
		
		chatRoom.setName(user.getUsername() + ", " + chatUser.getUsername());
		chatRoom.setIsPublic(false);
		chatRoom.setIsGroup(false);
		chatRoom.getJoinedUsers().add(user);
		chatRoom.getJoinedUsers().add(chatUser);
		
		return repository.save(chatRoom);
	}
	
	public ChatRoom create() {
		ChatRoom chatRoom = new ChatRoom();
		
		chatRoom.setName("Public");
		chatRoom.setIsPublic(true);
		chatRoom.setIsGroup(false);
		
		return repository.save(chatRoom);
	}
	
	public ChatRoom create(ChatForm chatForm) {
		ChatRoom chatRoom = new ChatRoom();
		
		chatRoom.setName(chatForm.getName());
		chatRoom.setIsGroup(chatForm.getIsGroup());
		chatRoom.setIsPublic(false);
		
		Optional<ChatUser> optionalSender = userRepository.findById(chatForm.getCreatedBy());
		
		if(optionalSender.isEmpty()) {
			return null;
		}
		
		ChatUser sender = optionalSender.get();
		
		List<ChatUser> users = new ArrayList<>();
		chatForm.getJoinedUserNames().forEach(username -> userRepository.findUserByUsernameIgnoreCase(username).ifPresent(users::add));
		chatRoom.getJoinedUsers().addAll(users);
		chatRoom.getJoinedUsers().add(sender);
		
		chatRoom = repository.save(chatRoom);
		
		ChatRoom tmpChatRoom1 = chatRoom;
		users.forEach(user -> {
			user.getJoinedRooms().add(tmpChatRoom1);
			userRepository.save(user);
		});
		
		sender.addRoom(chatRoom);
		userRepository.save(sender);
		
		return chatRoom;
	}
	
	public Optional<ChatRoom> getPublicRoomForFrontEnd() {
		return this.getPublicRoom().map(this::prepareRoomForFrontEnd).or(() -> Optional.of(this.create()));
	}
	
	public Optional<ChatRoom> getPublicRoom() {
		return repository.findChatRoomByIsPublicIsTrue();
	}
	
	public List<ChatRoom> getUserRooms(String username) {
		List<ChatRoom> rooms = this.repository.findByJoinedUsers_Username(username);
		
		for (ChatRoom chatRoom : rooms) {
			this.prepareRoomForFrontEnd(chatRoom);
		}
		
		return rooms;
	}
	
	public ChatRoom update(ChatRoom chatRoom) {
		return repository.save(chatRoom);
	}
	
	public Optional<ChatRoom> get(Integer id) {
		return this.repository.findById(id);
	}
	
	public Optional<ChatRoom> getChatRoomForFrontEnd(Integer id) {
		return prepareRoomForFrontEnd(this.get(id));
	}
	
	private Optional<ChatRoom> prepareRoomForFrontEnd(Optional<ChatRoom> optionalRoom) {
		return optionalRoom.map(this::prepareRoomForFrontEnd);
	}
	
	public ChatRoom prepareRoomForFrontEnd(ChatRoom room) {
		room.getJoinedUsers().forEach(user -> {
			user.setJoinedRooms(new ArrayList<>());
			user.setMessages(new ArrayList<>());
			user.setPassword(null);
		});
		room.getMessages().forEach(message -> {
			message.setRoom(null);
			message.getUser().setMessages(new ArrayList<>());
			message.getUser().setJoinedRooms(new ArrayList<>());
		});
		
		return room;
	}
}
