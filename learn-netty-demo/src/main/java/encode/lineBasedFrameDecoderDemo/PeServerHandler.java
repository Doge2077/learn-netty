package encode.lineBasedFrameDecoderDemo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PeServerHandler extends SimpleChannelInboundHandler {

    private int counter;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("server channelActive");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = (String)msg;
        System.out.println("-----start------| "+ message + " | ------end------");

        String content = "receive" + ++counter + "\n";
        ctx.writeAndFlush(content);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }

}
