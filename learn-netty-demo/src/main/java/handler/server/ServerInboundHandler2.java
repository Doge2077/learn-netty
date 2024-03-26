package handler.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ServerInboundHandler2 extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerInboundHandler2.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("ServerInboundHandler2 channelActive-----");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("ServerInboundHandler2 channelRead----,remoteAddress={}",ctx.channel().remoteAddress());
        //处理接收的数据
        ByteBuf buf = (ByteBuf) msg;
        log.info("ServerInboundHandler2:received client data = {}",buf.toString(StandardCharsets.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("ServerInboundHandler2 channelReadComplete----");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }
}