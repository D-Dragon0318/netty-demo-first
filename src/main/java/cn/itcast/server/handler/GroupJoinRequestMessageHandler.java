package cn.itcast.server.handler;

import cn.itcast.message.GroupJoinResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @Author: Spridra
 * @CreateTime: 2024-06-29 11:46
 * @Describe:
 * @Version: 1.0
 */

public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinResponseMessage msg) throws Exception {

    }
}
