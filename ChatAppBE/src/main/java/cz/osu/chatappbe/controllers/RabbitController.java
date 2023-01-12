package cz.osu.chatappbe.controllers;

import com.google.gson.Gson;
import cz.osu.chatappbe.models.DB.Message;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@CrossOrigin
public class RabbitController {
	@Autowired
	private AmqpTemplate rabbitTemplate;
	@Autowired
	private AmqpAdmin admin;
	
	@GetMapping(value = "/api/queue")
	public List<Message> getMessage(@RequestParam String username) {
		List<Message> receivedMessages = new ArrayList<>();
		while (Objects.requireNonNull(admin.getQueueInfo("public-queue-" + username)).getMessageCount() != 0) {
			/*String foo = (String) rabbitTemplate.receiveAndConvert("public-queue-" + username);
			Gson gson = new Gson();
			Message message = gson.fromJson(foo, Message.class);
			receivedMessages.add(message);*/
			//receivedMessages.add((Message) rabbitTemplate.receiveAndConvert("public-queue-" + username));
			System.out.println(rabbitTemplate.receiveAndConvert("public-queue-" + username));
		}
		return receivedMessages;
	}
}
