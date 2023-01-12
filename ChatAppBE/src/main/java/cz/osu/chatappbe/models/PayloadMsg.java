package cz.osu.chatappbe.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PayloadMsg {
	private Integer senderId;
	private Integer chatId;
	private String content;
	private String date;
	
}
