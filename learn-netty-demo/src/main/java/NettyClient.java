import handler.client.ClientInboundHandler1;
import handler.client.ClientSimpleInboundHandler2;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient {

    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);

    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        client.start("127.0.0.1", 8888);
    }

    public void start(String host, int port) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //添加客户端 channel 对应的 handler
                            pipeline.addLast(new ClientInboundHandler1());
                            pipeline.addLast(new ClientSimpleInboundHandler2());
                        }
                    });
            //连接远程启动
            ChannelFuture future = bootstrap.connect(host, port).sync();
            //监听通道关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty client error ,msg={}", e.getMessage());
        } finally {
            //优雅关闭
            group.shutdownGracefully();
        }
    }
}