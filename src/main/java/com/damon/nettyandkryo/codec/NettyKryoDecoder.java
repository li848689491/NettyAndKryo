package com.damon.nettyandkryo.codec;

import com.damon.nettyandkryo.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 自定义解码器
 * @author damon
 */
public class NettyKryoDecoder extends ByteToMessageDecoder {

    Logger log = LoggerFactory.getLogger(getClass());
    private static final int BODY_LENGTH = 4;
    private final Serializer serializer;
    private final Class<?> genericClass;

    public NettyKryoDecoder(Serializer serializer, Class<?> genericClass) {
        this.serializer = serializer;
        this.genericClass = genericClass;
    }

    /**
     *
     * @param channelHandlerContext 解码器关联的 ChannelHandlerContext 对象
     * @param byteBuf "入站"数据，也就是 ByteBuf 对象
     * @param list 解码之后的数据对象需要添加到 out 对象里面
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        //因为消息长度所占字节为4，则得到的入站数据必须要大于4
        if(byteBuf.readableBytes() >= BODY_LENGTH){
            //标记当前readIndex的位置，方便后面重置
            byteBuf.readerIndex();
            //读取消息长度
            int dataLength = byteBuf.readInt();
            //情况判断
            if(dataLength < 0 || byteBuf.readableBytes() < 0){
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }
            //如果可读字节数小于消息长度的话，说明不是完整的消息，重置ReaderIndex
            if(byteBuf.readableBytes() < dataLength){
                byteBuf.resetReaderIndex();
                return;
            }
            //到这里就可以正常反序列化了
            byte[] body = new byte[dataLength];
            byteBuf.readBytes(body);
            Object object = serializer.deserialize(body, genericClass);
            list.add(object);
            log.info("successful decode ByteBuf to Object");
        }

    }
}
