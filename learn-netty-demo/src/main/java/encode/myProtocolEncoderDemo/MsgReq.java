package encode.myProtocolEncoderDemo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MsgReq {

    private byte type;

    private int length;

    private String content;


}
