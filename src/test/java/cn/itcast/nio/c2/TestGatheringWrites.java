package cn.itcast.nio.c2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

import static cn.itcast.nio.c2.ByteBufferUtil.debugAll;

public class TestGatheringWrites {
    public static void main(String[] args) {
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("你好");

        try (FileChannel channel = new RandomAccessFile("words.txt", "rw").getChannel()) {
            channel.position(23);
            channel.write(new ByteBuffer[]{b1, b2, b3});
        } catch (IOException e) {
        }
        try (FileChannel channel1 = new RandomAccessFile("words.txt", "rw").getChannel()){
            ByteBuffer byteBuffer = ByteBuffer.allocate(100);
            channel1.read(byteBuffer);
            // byteBuffer.flip();
            debugAll(byteBuffer);
            System.out.println(channel1);
        }catch (IOException e) {
        }
    }
}
