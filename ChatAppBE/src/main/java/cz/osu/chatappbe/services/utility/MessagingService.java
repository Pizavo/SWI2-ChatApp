package cz.osu.chatappbe.services.utility;

import cz.osu.chatappbe.config.RabbitMQConfig;
import cz.osu.chatappbe.models.DB.ChatRoom;
import cz.osu.chatappbe.models.DB.ChatUser;
import cz.osu.chatappbe.models.DB.Message;
import cz.osu.chatappbe.models.PayloadMsg;
import cz.osu.chatappbe.services.models.ChatRoomService;
import cz.osu.chatappbe.services.models.MessageService;
import cz.osu.chatappbe.services.models.UserService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
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
	
	public PayloadMsg receivePublicMessage(PayloadMsg msg) {
		Optional<ChatUser> user = userService.get(msg.getSenderId());
		Optional<ChatRoom> room = chatRoomService.getPublicRoom();
		
		if (user.isEmpty() || room.isEmpty()) {
			return null;
		}
		
		//ChatUser user = user.get();
		Message publicMessage = messageService.create(user.get(), room.get(), msg.getContent(), msg.getDate());
		
		/*ChatRoom receivingRoom = new ChatRoom(dbService.getPublicChatroom());
		receivingRoom.setMessages(new ArrayList<>());
		receivingRoom.setJoinedUsers(new ArrayList<>());
		publicMessage.setChatRoom(receivingRoom);
		
		ChatUser senderUser = new ChatUser(dbService.getUser(msg.getSenderName()).get());
		senderUser.setMessages(new ArrayList<>());
		senderUser.setJoinedRooms(new ArrayList<>());
		publicMessage.setChatUser(senderUser);*/
		
		this.send(RabbitMQConfig.exchange, publicMessage);
		
		return msg;
	}
	
	public PayloadMsg receiveGroupMessage(@Payload PayloadMsg msg) {
		// todo: poslat message do queue od všech uživatelů kteří jsou u dané ChatRoomId (GroupId)
		// todo: asi ulozit zpravu do databaze (vzdy ji uklada ten, co ji odesila)
		// todo: poslat web socket zpravu aby si uzivatel vyzvedl zpravu z queue pokud je prihlaseny
		
		Optional<ChatUser> optionalUser = userService.get(msg.getSenderId());
		Optional<ChatRoom> optionalRoom = chatRoomService.get(msg.getChatId());
		
		if (optionalUser.isEmpty() || optionalRoom.isEmpty()) {
			return null;
		}
		
		ChatUser user = optionalUser.get();
		ChatRoom room = optionalRoom.get();
		
		Message message = messageService.create(user, room, msg.getContent(), msg.getDate());
		
		System.out.println(user.getId());
		
		room.getJoinedUsers().forEach(u -> {
			System.out.println(u.getId());
			if (!u.getId().equals(user.getId())) {
				this.send("public-queue-" + u.getUsername(), message);
				System.out.println(" sent");
			}
		});
		
		/*String destination = "/chatroom/" + msg.getChatId();
		simpMessagingTemplate.convertAndSend(destination, msg);*/
		return msg;
	}
	
	public PayloadMsg receivePrivateMessage(@Payload PayloadMsg msg) {
		// todo: poslat do queue od sendera a receivera
		// todo: asi ulozit zpravu do databaze (vzdy ji uklada ten, co ji odesila)
		// todo: poslat web socket zpravu aby si uzivatel vyzvedl zpravu z queue pokud je prihlaseny
		
		Optional<ChatUser> optionalSender = userService.get(msg.getSenderId());
		//Optional<ChatUser> optionalReceiver = userService.get(msg.getReceiverChatId());
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
		
		// url: /user/username/private
		//simpMessagingTemplate.convertAndSendToUser(optionalReceiver.get().getUsername(), "/private", msg);
		this.send("public-queue-" + optionalReceiver.get().getUsername(), message);
		return msg;
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
