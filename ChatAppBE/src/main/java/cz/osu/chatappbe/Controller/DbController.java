package cz.osu.chatappbe.Controller;

import cz.osu.chatappbe.Model.DB.ChatRoom;
import cz.osu.chatappbe.Model.DB.ChatUser;
import cz.osu.chatappbe.Service.DbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class DbController {

    @Autowired
    private DbService dbService;

    @GetMapping(value = "/users")
    public ResponseEntity<List<ChatUser>> getUsers() {
        return dbService.getUsers();
    }

    @GetMapping(value="/chatrooms")
    public ResponseEntity<List<ChatRoom>> getChatRooms(@RequestParam String username) {
        return dbService.getChatRooms(username);
    }

}
