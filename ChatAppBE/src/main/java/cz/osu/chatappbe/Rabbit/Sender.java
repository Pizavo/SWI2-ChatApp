package cz.osu.chatappbe.rabbit;

import com.google.gson.Gson;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Sender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void sendPublic(String exchange, String msg) {
        Gson gson = new Gson();
        String msgJson = gson.toJson(msg);
        rabbitTemplate.convertAndSend(exchange, "", msgJson);
    }
}
