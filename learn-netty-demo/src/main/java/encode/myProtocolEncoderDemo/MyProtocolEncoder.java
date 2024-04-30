package encode.myProtocolEncoderDemo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;


public class MyProtocolEncoder extends MessageToByteEncoder {



    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        MsgReq req = (MsgReq) msg;
        out.writeByte(req.getType());
        out.writeInt(req.getLength());
        out.writeBytes(req.getContent().getBytes(StandardCharsets.UTF_8));
    }
}
