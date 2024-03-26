package handler.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ServerInboundHandler1 extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerInboundHandler1.class);

    /**
     * 通道准备就绪时
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("ServerInboundHandler1 channelActive-----");

        //将事件向下传递
        ctx.fireChannelActive();
    }

    /**
     * 通道有数据可读时
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("ServerInboundHandler1 channelRead----,remoteAddress={}", ctx.channel().remoteAddress());
        //处理接收的数据
        ByteBuf buf = (ByteBuf) msg;
        log.info("ServerInboundHandler1:received client data = {}", buf.toString(StandardCharsets.UTF_8));

        //将事件消息向下传递，如果不传递则 msg 不会到达下一个 handler
        ctx.fireChannelRead(msg);
    }

    /**
     * 数据读取完毕时
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("channelReadComplete----");
        //数据读取结束后向客户端写回数据
        byte[] data = "hello client , i am server".getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = Unpooled.buffer(data.length);
        buffer.writeBytes(data);//以bytebuf为中心,看是写到bytebuf中还是从bytebuf中读
        ByteBuf buf = Unpooled.copiedBuffer("hello client , i am server", StandardCharsets.UTF_8);
        ctx.writeAndFlush(buf);//通过ctx写，事件会从当前handler向pipeline头部移动
        //ctx.channel().writeAndFlush(buf);//通过Channel写,事件会从通道尾部向头部移动
    }

    /**
     * 发生异常时
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("ServerInboundHandler1 exceptionCaught----,cause={}", cause.getMessage());
    }
}