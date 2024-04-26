package feture;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NettyFutureTest {
    private static final Logger log = LoggerFactory.getLogger(NettyFutureTest.class);


    @Test
    public void testFuture() throws InterruptedException, ExecutionException {
        EventLoopGroup group = new NioEventLoopGroup();

        Future<String> future = group.submit( () ->{
            log.info("---异步线程执行任务开始----,time={}", LocalDateTime.now().toString());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("---异步线程执行任务结束----,time={}", LocalDateTime.now().toString());
            return "hello netty future";
        });
        /*String result = future.get();
        log.info("----主线程阻塞等待异步线程执行结果:{}",result);*/
        //设置监听
        future.addListener( future1 -> {
            log.info("---收到异步线程执行任务结果通知----执行结果是;{},time={}",future1.get(), LocalDateTime.now().toString());
        });
        log.info("---主线程----");
        TimeUnit.SECONDS.sleep(10);
    }


    @Test
    public void testPromise() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        Promise promise = new DefaultPromise(group.next());//promise绑定到eventloop上

        group.submit(()->{
            log.info("---异步线程执行任务开始----,time={}", LocalDateTime.now().toString());
            try {
                int i = 1/0;
                TimeUnit.SECONDS.sleep(3);
                promise.setSuccess("hello netty promise");
                TimeUnit.SECONDS.sleep(3);
                log.info("---异步线程执行任务结束----,time={}", LocalDateTime.now().toString());
                return;
            } catch (Exception e) {
                promise.setFailure(e);
            }
        });
        //设置监听回调
        promise.addListener(future -> {
            log.info("----异步任务执行结果:{}",future.isSuccess());
        });
        promise.addListener(future2 -> {
            log.info("----异步任务执行结果:{}",future2.isSuccess());
        });
        log.info("---主线程----");
        TimeUnit.SECONDS.sleep(10);
    }

}
