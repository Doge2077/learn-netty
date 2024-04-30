package codec;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ProtoStuffDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try {
            int length = msg.readableBytes();
            byte[] bytes = new byte[length];
            msg.readBytes(bytes);
            UserInfo userInfo = ProtostuffUtil.deserialize(bytes, UserInfo.class);
            out.add(userInfo);
        } catch (Exception e) {
            log.error("protostuff decode error,msg={}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
