package cz.osu.chatappbe.rabbit;

import cz.osu.chatappbe.models.DB.Message;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    public void receiveMessage(String msg) {
        System.out.println("Received message: " + msg);
    }
}
