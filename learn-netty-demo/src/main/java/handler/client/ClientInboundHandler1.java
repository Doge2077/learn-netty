package handler.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ClientInboundHandler1 extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ClientInboundHandler1.class);

    /**
     * 通道准备就绪
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("ClientInboundHandler1 channelActive begin send data");
        //通道准备就绪后开始向服务端发送数据
        ByteBuf buf = Unpooled.copiedBuffer("hello server,i am client".getBytes(StandardCharsets.UTF_8));
        ctx.writeAndFlush(buf);
    }

    /**
     * 通道有数据可读（服务端返回了数据）
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("ClientInboundHandler1 channelRead");
        ByteBuf buf = (ByteBuf) msg;
        log.info("ClientInboundHandler1: received server data ={}", buf.toString(StandardCharsets.UTF_8));

        // 接着传递消息给下一个ChannelInboundHandler
        ctx.fireChannelRead(msg);
    }

    /**
     * 数据读取完毕
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    /**
     * 产生了异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}