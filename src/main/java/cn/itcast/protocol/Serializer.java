package cn.itcast.protocol;

import cn.itcast.message.Message;

import java.io.*;

/**
 * @Author: Spridra
 * @CreateTime: 2024-06-29 17:05
 * @Describe: 用于扩展序列化、反序列化算法
 * @Version: 1.0
 */
public interface Serializer {
    //反序列化方法，输入字节数组，输出对象
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    //序列化方法，输入对象，输出字节数组
    <T> byte[] serialize(T object);

    enum Algorithm implements Serializer {
        Java {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {
                try {
                    ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                    return (T) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("反序列化失败",e);
                }
            }

            @Override
            public <T> byte[] serialize(T object) {
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return  bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("序列化失败",e);
                }
            }
        }
    }
}
