package cz.osu.chatappbe.Model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PayloadMsg {
    private String senderName;
    private String receiverName;
    private String receiverGroupId;
    private String content;
    private String date;

}
