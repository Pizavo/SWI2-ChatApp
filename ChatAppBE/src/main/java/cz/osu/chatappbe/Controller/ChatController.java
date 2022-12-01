package cz.osu.chatappbe.Controller;

import cz.osu.chatappbe.Config.RabbitMQConfig;
import cz.osu.chatappbe.Model.DB.ChatRoom;
import cz.osu.chatappbe.Model.DB.ChatUser;
import cz.osu.chatappbe.Model.DB.Message;
import cz.osu.chatappbe.Model.PayloadMsg;
import cz.osu.chatappbe.Rabbit.Sender;
import cz.osu.chatappbe.Service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.transaction.Transactional;
import java.util.Calendar;

@Controller
@Transactional
public class ChatController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    private DbService dbService;
    @Autowired
    private Sender sender;

    @MessageMapping("/checkPublic")
    public boolean checkPublicMessages() {
        return true;
    }

    @MessageMapping("/message")
    @SendTo("/chatroom/public")
    public PayloadMsg receivePublicMessage(@Payload PayloadMsg msg) {
        // Prevedeni message objektu z front-endu do back-endoveho objektu
        Message publicMessage = new Message();
        publicMessage.setContent(msg.getContent());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(msg.getDate()));
        publicMessage.setSendTime(calendar.getTime());

        ChatRoom receivingRoom = new ChatRoom(dbService.getPublicChatroom());
        receivingRoom.setMessages(null);
        receivingRoom.setJoinedUsers(null);
        publicMessage.setChatRoom(receivingRoom);

        ChatUser senderUser = new ChatUser(dbService.getUser(msg.getSenderName()));
        senderUser.setMessages(null);
        senderUser.setJoinedRooms(null);
        publicMessage.setChatUser(senderUser);

        // todo: ukladani do databaze (celkove by byla treba pozmenit databaze)
        /*
        dbService.getUser(msg.getSenderName()).addMessage(message);
        dbService.getPublicChatroom().addMessage(message);
        dbService.getMessageRepository().save(message);
        */

        // Odeslani zpravy do vsech front prizazenych k dane exchangi (zde se jedna o public zpravu, tudiz vsem)
        sender.sendPublic(RabbitMQConfig.exchange, String.valueOf(publicMessage));      // TODO - hází error bez String.valueOf ??

        return msg;
    }

    @MessageMapping("/group-message")
    public PayloadMsg receiveGroupMessage(@Payload PayloadMsg msg) {
        // todo: poslat message do queue od všech uživatelů kteří jsou u dané ChatRoomId (GroupId)
        // todo: asi ulozit zpravu do databaze (vzdy ji uklada ten, co ji odesila)
        // todo: poslat web socket zpravu aby si uzivatel vyzvedl zpravu z queue pokud je prihlaseny

        String destination = "/chatroom/" + msg.getReceiverGroupId();
        simpMessagingTemplate.convertAndSend(destination, msg);
        return msg;
    }

    @MessageMapping("/private-message")
    public PayloadMsg receivePrivateMessage(@Payload PayloadMsg msg) {
        // todo: poslat do queue od sendera a receivera
        // todo: asi ulozit zpravu do databaze (vzdy ji uklada ten, co ji odesila)
        // todo: poslat web socket zpravu aby si uzivatel vyzvedl zpravu z queue pokud je prihlaseny

        // url: /user/username/private
        simpMessagingTemplate.convertAndSendToUser(msg.getReceiverName(), "/private", msg);
        return msg;
    }

}
