package handler.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ServerOutboundHandler1 extends ChannelOutboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(ServerOutboundHandler1.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        log.info("OutboundHandler1----server send msg to client,msg ={}", buf.toString(StandardCharsets.UTF_8));
//        //利用channel在outboundhandler中再写数据会引起类似递归的调用，数据再从pipeline尾部流向头部
//        ctx.channel().writeAndFlush(Unpooled.copiedBuffer("114514".getBytes(StandardCharsets.UTF_8)));

        //将事件向前传播,父类中调用了ctx.write(msg,promise);
        super.write(ctx, buf, promise);

        //用ctx写数据，代码写到super之后，该事件会流到该handler之前的handler进行处理
        ctx.writeAndFlush(Unpooled.copiedBuffer("--- solved by ServerOutboundHandler1".getBytes(StandardCharsets.UTF_8)));
    }
}