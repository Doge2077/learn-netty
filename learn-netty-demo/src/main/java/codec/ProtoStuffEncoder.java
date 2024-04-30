package codec;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ProtoStuffEncoder extends MessageToMessageEncoder<UserInfo> {

    @Override
    protected void encode(ChannelHandlerContext ctx, UserInfo msg, List<Object> out) throws Exception {
        try {
            byte[] bytes = ProtostuffUtil.serialize(msg);
            ByteBuf buffer = ctx.alloc().buffer(bytes.length);
            buffer.writeBytes(bytes);
            out.add(buffer);
        } catch (Exception e) {
            log.error("protostuff encode error,msg={}",e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
