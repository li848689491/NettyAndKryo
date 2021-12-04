package com.damon.nettyandkryo.serializer;

import com.damon.nettyandkryo.entity.RequestMessage;
import com.damon.nettyandkryo.entity.ResponseMessage;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 自定义kryo 序列化实现类
 * @author damon
 */
public class KryoSerializer implements Serializer{

    Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 由于kryo不是线程安全的，则每个线程都应该有自己的kryo，Input或Output实例
     * 所以，使用ThreadLocal存放Kryo对象
     * 这样九九减少了每次使用都实例化一次Kryo的开销，又能保证线程安全
     */
    public static final ThreadLocal<Kryo> KryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        kryo.register(RequestMessage.class);
        kryo.register(ResponseMessage.class);
        return kryo;
    });


    /**
     * 序列化
     * @param obj 要序列化的对象
     * @return
     */
    @Override
    public byte[] serialize(Object obj) {
        try{
            Kryo kryo = KryoThreadLocal.get();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream);
            // Object->byte: 将对象序列化为 byte 数组
            kryo.writeObject(output, obj);
            KryoThreadLocal.remove();
            log.info("序列化成功");
            return output.toBytes();
        } catch (Exception e) {
            throw new RuntimeException("序列化失败");
        }
    }

    /**
     * 反序列化
     * @param bytes 序列化后的字节数组
     * @param clazz clazz 类
     * @param <T>
     * @return
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            Kryo kryo = KryoThreadLocal.get();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
            // byte->Object: 从 byte 数组中反序列化出对象
            Object o = kryo.readObject(input, clazz);
            KryoThreadLocal.remove();
            log.info("反序列化成功");
            return clazz.cast(o);
        } catch (Exception e) {
            throw new RuntimeException("反序列化失败");
        }
    }
}
