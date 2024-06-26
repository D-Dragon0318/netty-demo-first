package cn.itcast.server.handler;

import cn.itcast.message.ChatRequestMessage;
import cn.itcast.message.ChatResponseMessage;
import cn.itcast.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author: Spridra
 * @CreateTime: 2024-06-25 17:57
 * @Describe:
 * @Version: 1.0
 */

@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession().getChannel(to);
        //在线
        if (channel != null){
            channel.writeAndFlush(new ChatRequestMessage(msg.getFrom(),msg.getContent()));
        }
        //不在线
        else {
            ctx.writeAndFlush(new ChatResponseMessage(false,"用户不存在或不在线"));
        }

    }
}
