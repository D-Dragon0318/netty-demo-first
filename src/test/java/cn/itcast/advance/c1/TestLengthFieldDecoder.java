package cn.itcast.advance.c1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class TestLengthFieldDecoder {
    public static void main(String[] args) {
        //测试编解码用
        EmbeddedChannel channel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(
                        1024, 0, 4, 1,4),
                new LoggingHandler(LogLevel.DEBUG)
        );

        //  4 个字节的内容长度， 实际内容
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        send(buffer, "Hello, world");
        send(buffer, "Hi!");
        channel.writeInbound(buffer);
    }

    private static void send(ByteBuf buffer, String content) {
        byte[] bytes = content.getBytes(); // 实际内容
        int length = bytes.length; // 实际内容长度
        //int有四个字节
        buffer.writeInt(length);
        //消息的额外内容
        buffer.writeByte(1);
        //实际的消息
        buffer.writeBytes(bytes);
    }
}

