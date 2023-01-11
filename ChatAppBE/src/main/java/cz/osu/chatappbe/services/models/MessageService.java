package cz.osu.chatappbe.services.models;

import cz.osu.chatappbe.models.DB.ChatRoom;
import cz.osu.chatappbe.models.DB.ChatUser;
import cz.osu.chatappbe.models.DB.Message;
import cz.osu.chatappbe.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class MessageService {
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
		
		/*chatRoom.setMessages(null);
		chatRoom.setJoinedUsers(null);
		
		user.setMessages(null);
		user.setJoinedRooms(null);*/
		
		message.setContent(content);
		message.setSendTime(date.getTime());
		message.setChatRoom(chatRoom);
		message.setChatUser(user);
		
		return repository.save(message);
	}
}
