package cz.osu.chatappbe.services.models;

import com.google.gson.Gson;
import cz.osu.chatappbe.models.DB.ChatRoom;
import cz.osu.chatappbe.models.DB.ChatUser;
import cz.osu.chatappbe.models.DB.Message;
import cz.osu.chatappbe.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;

@Service
public class MessageService {
	private final Gson gson = new Gson();
	@Autowired
	private MessageRepository repository;
	
	public Message create(ChatUser user, ChatRoom chatRoom, String content, String date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(Long.parseLong(date));
		
		return this.create(user, chatRoom, content, calendar);
	}
	
	public Message create(ChatUser user, ChatRoom chatRoom, String content) {
		return this.create(user, chatRoom, content, Calendar.getInstance());
	}
	
	private Message create(ChatUser user, ChatRoom chatRoom, String content, Calendar date) {
		Message message = new Message();
		
		message.setContent(content);
		message.setSendTime(date.getTime());
		message.setRoom(chatRoom);
		message.setUser(user);
		
		message = repository.save(message);
		
		ChatRoom chatRoom1 = ChatRoom.builder()
		                             .id(chatRoom.getId())
		                             .isGroup(chatRoom.getIsGroup())
		                             .isPublic(chatRoom.getIsPublic())
		                             .name(chatRoom.getName())
		                             .joinedUsers(new ArrayList<>())
		                             .build();
		
		ChatUser chatUser = ChatUser.builder()
		                            .id(user.getId())
		                            .username(user.getUsername())
		                            .messages(new ArrayList<>())
		                            .joinedRooms(new ArrayList<>())
		                            .build();
		
		return Message.builder()
		              .id(message.getId())
		              .content(message.getContent())
		              .sendTime(message.getSendTime())
		              .user(chatUser)
		              .room(chatRoom1)
		              .build();
	}
	
	public String prepareForRabbit(Message message) {
		message.getRoom().setMessages(new ArrayList<>());
		message.getRoom().setJoinedUsers(new ArrayList<>());
		message.getUser().setMessages(new ArrayList<>());
		message.getUser().setJoinedRooms(new ArrayList<>());
		
		/*message.setRoom(null);
		message.setUser(null);*/
		
		return this.gson.toJson(message);
	}
	
	public Message receiveFromRabbit(Object message) {
		return this.gson.fromJson((String) message, Message.class);
	}
}
