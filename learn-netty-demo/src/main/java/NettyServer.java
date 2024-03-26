import handler.server.ServerInboundHandler1;
import handler.server.ServerInboundHandler2;
import handler.server.ServerOutboundHandler1;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    public static void main(String[] args) {
        NettyServer NettyServer = new NettyServer();
        NettyServer.start(8888);
    }

    public void start(int port) {
        //创建 bossGroup workerGroup 分别管理连接建立事件和具体的业务处理事件
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //创建启动引导类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //配置参数
            serverBootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)       //指定服务端通道，用于接收并创建新连接
                    .handler(new LoggingHandler(LogLevel.DEBUG)) // 给 boss group 配置 handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //每个客户端 channel 初始化时都会执行该方法来配置该 channel 的相关 handler
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //获取与该 channel 绑定的 pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            //向 pipeline 中添加 handler，如果没有注册到这里则不会生效
                            pipeline.addLast(new ServerOutboundHandler1());
                            pipeline.addLast(new ServerInboundHandler1());
                            pipeline.addLast(new ServerInboundHandler2());
                        }
                    }); //给 worker group 配置 handler
            //服务端绑定端口启动
            ChannelFuture future = serverBootstrap.bind(port).sync();
            //服务端监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("netty server error ,{}",e.getMessage());
        } finally {
            //优雅关闭 boss worker
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}