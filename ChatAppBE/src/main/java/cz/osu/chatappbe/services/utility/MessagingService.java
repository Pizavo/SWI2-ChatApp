package cz.osu.chatappbe.services.utility;

import cz.osu.chatappbe.models.DB.ChatRoom;
import cz.osu.chatappbe.models.DB.ChatUser;
import cz.osu.chatappbe.models.DB.Message;
import cz.osu.chatappbe.models.PayloadMsg;
import cz.osu.chatappbe.models.PayloadReply;
import cz.osu.chatappbe.services.models.ChatRoomService;
import cz.osu.chatappbe.services.models.MessageService;
import cz.osu.chatappbe.services.models.UserService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MessagingService {
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired
	private AmqpTemplate rabbitTemplate;
	@Autowired
	private AmqpAdmin admin;
	@Autowired
	private MessageService messageService;
	@Autowired
	private UserService userService;
	@Autowired
	private ChatRoomService chatRoomService;
	
	public PayloadReply receivePublicMessage(PayloadMsg msg) {
		/*Optional<ChatUser> user = userService.get(msg.getSenderId());
		Optional<ChatRoom> room = chatRoomService.getPublicRoom();
		
		if (user.isEmpty() || room.isEmpty()) {
			return null;
		}
		
		Message message = messageService.create(user.get(), room.get(), msg.getContent(), msg.getDate());
		
		this.send(RabbitMQConfig.exchange, message);
		
		return msg;*/
		
		return this.receiveMessage(msg);
	}
	
	public PayloadReply receiveGroupMessage(PayloadMsg msg) {
		// todo: poslat message do queue od všech uživatelů kteří jsou u dané ChatRoomId (GroupId)
		// todo: asi ulozit zpravu do databaze (vzdy ji uklada ten, co ji odesila)
		// todo: poslat web socket zpravu aby si uzivatel vyzvedl zpravu z queue pokud je prihlaseny
		
		/*
		Optional<ChatUser> optionalUser = userService.get(msg.getSenderId());
		Optional<ChatRoom> optionalRoom = chatRoomService.get(msg.getChatId());
		
		if (optionalUser.isEmpty() || optionalRoom.isEmpty()) {
			return null;
		}
		
		ChatUser user = optionalUser.get();
		ChatRoom room = optionalRoom.get();
		
		Message message = messageService.create(user, room, msg.getContent(), msg.getDate());
		
		room.getJoinedUsers().forEach(u -> {
			//if (!u.getId().equals(user.getId())) {
				this.send("queue-" + u.getUsername(), message);
			//}
		});

//		String destination = "/chatroom/" + msg.getChatId();
//		simpMessagingTemplate.convertAndSend(destination, msg);
		return msg;*/
		
		return this.receiveMessage(msg);
	}
	
	private PayloadReply receiveMessage(PayloadMsg msg) {
		Optional<ChatUser> optionalUser = userService.get(msg.getSenderId());
		Optional<ChatRoom> optionalRoom = chatRoomService.get(msg.getChatId());
		
		if (optionalUser.isEmpty() || optionalRoom.isEmpty()) {
			return null;
		}
		
		ChatUser user = optionalUser.get();
		ChatRoom room = optionalRoom.get();
		
		Message message = messageService.create(user, room, msg.getContent(), msg.getDate());
		
		room.getJoinedUsers().forEach(u -> {
			this.send("queue-" + u.getUsername(), message);
		});
		
		return new PayloadReply(msg, room.getId());
	}
	
	public PayloadReply receivePrivateMessage(PayloadMsg msg) {
		// todo: poslat do queue od sendera a receivera
		// todo: asi ulozit zpravu do databaze (vzdy ji uklada ten, co ji odesila)
		// todo: poslat web socket zpravu aby si uzivatel vyzvedl zpravu z queue pokud je prihlaseny
		
		/*Optional<ChatUser> optionalSender = userService.get(msg.getSenderId());
		Optional<ChatRoom> optionalRoom = chatRoomService.get(msg.getChatId());
		
		if (optionalSender.isEmpty() || optionalRoom.isEmpty()) {
			return null;
		}
		
		ChatUser sender = optionalSender.get();
		ChatRoom room = optionalRoom.get();
		
		Optional<ChatUser> optionalReceiver = room.getJoinedUsers().stream().filter(u -> !u.getId().equals(sender.getId())).findFirst();
		
		if (optionalReceiver.isEmpty()) {
			return null;
		}
		
		Message message = messageService.create(sender, room, msg.getContent(), msg.getDate());

//		 url: /user/username/private
//		simpMessagingTemplate.convertAndSendToUser(optionalReceiver.get().getUsername(), "/private", msg);
		this.send("queue-" + optionalReceiver.get().getUsername(), message);
		return msg;*/
		
		return this.receiveMessage(msg);
	}
	
	public void send(String exchange, Message message) {
		rabbitTemplate.convertAndSend(exchange, messageService.prepareForRabbit(message));
	}
	
	public List<Message> receive(String queueName) {
		List<Message> receivedMessages = new ArrayList<>();
		
		while (Objects.requireNonNull(admin.getQueueInfo(queueName)).getMessageCount() != 0) {
			receivedMessages.add(messageService.receiveFromRabbit(rabbitTemplate.receiveAndConvert(queueName)));
		}
		
		return receivedMessages;
	}
}
