package com.damon.nettyandkryo.client;

import com.damon.nettyandkryo.entity.RequestMessage;
import com.damon.nettyandkryo.entity.ResponseMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 客户端启动类
 * @author damon
 */
public class NettyClient {
    Logger log = LoggerFactory.getLogger(getClass());

    private String host;
    private int port;

    public NettyClient( String host,int port) {
        this.port = port;
        this.host = host;
    }

    private static Bootstrap bootstrap;
    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }
    public ResponseMessage sendMessage(RequestMessage requestMessage) {
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            log.info("client connect  {}", host + ":" + port);
            Channel channel = channelFuture.channel();
            if (channel != null) {
                // 客户端发送消息
                channel.writeAndFlush(requestMessage);
                // 添加 监听事件（TCP 连接是否断开）
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            log.info("client send message: [{}]", requestMessage.toString());
                        } else {
                            log.error("Send failed:", channelFuture.cause());
                        }
                    }
                });
                // 阻塞等待 ，直到Channel关闭
                channel.closeFuture().sync();
                // 将服务端返回的数据也就是ResponseMessage对象取出
                AttributeKey<ResponseMessage> response = AttributeKey.valueOf("ResponseMessage");
                return channel.attr(response).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 启动客户端并发送消息
     * @param args
     */
    public static void main(String[] args) {
        RequestMessage requestMessage = new RequestMessage("接口", "方法");
        NettyClient nettyClient = new NettyClient("localhost", 8001);
        // 客户端发送 3 次消息给服务端
        for (int i = 0; i < 3; i++) {
            nettyClient.sendMessage(requestMessage);
        }
        // 再发送 1 次
        ResponseMessage message = nettyClient.sendMessage(requestMessage);
        System.out.println(message.toString());

    }
}
