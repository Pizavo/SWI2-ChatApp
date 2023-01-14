package cz.osu.chatappbe.models;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PayloadReply {
	private PayloadMsg msg;
	private Integer chatId;
}
