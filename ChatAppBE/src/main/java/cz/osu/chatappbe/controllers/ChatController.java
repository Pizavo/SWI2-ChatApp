package cz.osu.chatappbe.controllers;

import cz.osu.chatappbe.models.PayloadMsg;
import cz.osu.chatappbe.models.PayloadReply;
import cz.osu.chatappbe.services.utility.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;

@Controller
@Transactional
public class ChatController {
	@Autowired
	private MessagingService messagingService;
	
	@MessageMapping("/checkPublic")
	public boolean checkPublicMessages() {
		return true;
	}
	
	@MessageMapping("/message")
//	@SendTo("/chatroom/public")
	@SendTo("!{'/chatroom/' + result.chatId}")
	public PayloadReply receivePublicMessage(@Payload PayloadMsg msg) {
		return messagingService.receivePublicMessage(msg);
	}
	
	@MessageMapping("/group-message")
	@SendTo("!{'/chatroom/' + result.chatId}")
	public PayloadReply receiveGroupMessage(@Payload PayloadMsg msg) {
		return messagingService.receiveGroupMessage(msg);
	}
	
	@MessageMapping("/private-message")
	@SendTo("!{'/chatroom/' + result.chatId}")
	public PayloadReply receivePrivateMessage(@Payload PayloadMsg msg) {
		return messagingService.receivePrivateMessage(msg);
	}
	
}
