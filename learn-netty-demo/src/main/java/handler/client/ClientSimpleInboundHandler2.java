package handler.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ClientSimpleInboundHandler2 extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger log = LoggerFactory.getLogger(ClientSimpleInboundHandler2.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        log.info("ClientSimpleInboundHandler2 channelRead");
        log.info("ClientSimpleInboundHandler2: received server data ={}", msg.toString(StandardCharsets.UTF_8));
    }

}