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
	@Autowired
	private MessageRepository repository;
	private final Gson gson = new Gson();
	
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
		
		/*chatRoom.setMessages(new ArrayList<>());
		chatRoom.setJoinedUsers(new ArrayList<>());
		
		user.setMessages(new ArrayList<>());
		user.setJoinedRooms(new ArrayList<>());*/
		
		message.setContent(content);
		message.setSendTime(date.getTime());
		message.setRoom(chatRoom);
		message.setUser(user);
		
		return repository.save(message);
	}
	
	public String prepareForRabbit(Message message) {
		message.getRoom().setMessages(new ArrayList<>());
		message.getRoom().setJoinedUsers(new ArrayList<>());
		message.getUser().setMessages(new ArrayList<>());
		message.getUser().setJoinedRooms(new ArrayList<>());
		
		return this.gson.toJson(message);
	}
	
	public Message receiveFromRabbit(Object message) {
		return this.gson.fromJson((String) message, Message.class);
	}
}
