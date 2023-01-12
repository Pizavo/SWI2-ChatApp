package cz.osu.chatappbe.models;

import cz.osu.chatappbe.models.DB.ChatUser;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChatForm {
	private String name;
	private Boolean isGroup;
	private List<String> joinedUserNames;
	private Integer createdBy;
}
