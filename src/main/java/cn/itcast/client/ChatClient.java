package cn.itcast.client;

import cn.itcast.message.*;
import cn.itcast.protocol.MessageCodecSharable;
import cn.itcast.protocol.ProcotolFrameDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        //并发工具类变量，用于多线程间的协调与状态管理
        /* CountDownLatch 是一个并发计数器工具类，允许一个或多个线程等待其他线程完成一系列操作。
        初始化时设置了一个计数值（本例中为1）。
        在这个上下文中，WAIT_FOR_LOGIN 可能用于阻塞某些线程直到登录过程完成。
        当登录操作完成时，通过调用 WAIT_FOR_LOGIN.countDown() 方法会将计数减1。
        如果计数到达0，所有在 await() 方法上等待的线程将被释放继续执行。 */
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        /* AtomicBoolean 是一个线程安全的布尔型原子类，用于在高并发环境下提供对布尔值的原子性读写操作，避免了同步块的使用。
        初始化为 false 表示初始状态未登录。
        这个变量可以用于快速检查登录状态，且支持原子性的更新操作，
        比如在登录成功时通过 LOGIN.set(true) 设置为 true，这样可以确保多线程环境下状态的正确性和一致性。 */
        AtomicBoolean LOGIN = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProcotolFrameDecoder());
                    // ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast("client handler",new ChannelInboundHandlerAdapter(){

                        @Override
                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                            log.debug("msg: {}",msg);
                            if (msg instanceof LoginResponseMessage){
                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                if (response.isSuccess()){
                                    LOGIN.set(true);
                                }
                                //不管成功还是失败都要唤醒 system in 线程
                                WAIT_FOR_LOGIN.countDown();
                            }
                        }

                        //连接建立后触发active
                        @Override
                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                            new Thread(()->{
                                Scanner scanner = new Scanner(System.in);
                                System.out.println("请输入用户名");
                                String username = scanner.nextLine();
                                System.out.println("请输入密码");
                                String password = scanner.nextLine();
                                //构造登录对象
                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(username, password);
                                ctx.writeAndFlush(loginRequestMessage);
                                System.out.println("等待后续操作");
                                //等待进一步输入
                                /* try {
                                    System.in.read();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                } */
                                try {
                                    WAIT_FOR_LOGIN.await();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!LOGIN.get()) {
                                    ctx.channel().close();
                                    return;
                                }
                                while (true){
                                    System.out.println("==================================");
                                    System.out.println("send [username] [content]");
                                    System.out.println("gsend [group name] [content]");
                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                    System.out.println("gmembers [group name]");
                                    System.out.println("gjoin [group name]");
                                    System.out.println("gquit [group name]");
                                    System.out.println("quit");
                                    System.out.println("==================================");
                                    String command = scanner.nextLine();
                                    String[] s = command.split(" ");
                                    switch (s[0]) {
                                        case "send":
                                            ctx.writeAndFlush(new ChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gsend":
                                            ctx.writeAndFlush(new GroupChatRequestMessage(username, s[1], s[2]));
                                            break;
                                        case "gcreate":
                                            Set<String> set = new HashSet<>(Arrays.asList(s[2].split(",")));
                                            set.add(username); // 加入自己
                                            ctx.writeAndFlush(new GroupCreateRequestMessage(s[1], set));
                                            break;
                                        case "gmembers":
                                            ctx.writeAndFlush(new GroupMembersRequestMessage(s[1]));
                                            break;
                                        case "gjoin":
                                            ctx.writeAndFlush(new GroupJoinRequestMessage(username, s[1]));
                                            break;
                                        case "gquit":
                                            ctx.writeAndFlush(new GroupQuitRequestMessage(username, s[1]));
                                            break;
                                        case "quit":
                                            ctx.channel().close();
                                            return;
                                    }
                                }
                            },"system in").start();
                        }
                    });
                }
            });
            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();
            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            //正常关闭
            group.shutdownGracefully();
        }
    }
}
