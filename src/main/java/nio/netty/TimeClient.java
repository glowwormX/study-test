package nio.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @author 徐其伟
 * @Description:
 * @date 19-2-19 下午4:51
 */
public class TimeClient {
    public static void main(String[] args) {
        try {
            new TimeClient().connect(8080, "localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connect(int port, String host) throws Exception{
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new TimeClientHandler());
                        }
                    });
            ChannelFuture channelFuture = b.connect(host, port).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private class TimeClientHandler extends ChannelHandlerAdapter {
        private ByteBuf msg;
        public TimeClientHandler() {
            byte[] req = ("first"+System.getProperty("line.separator")).getBytes();
            msg = Unpooled.buffer(req.length);
            msg.writeBytes(req);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(msg);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            byte[] resBytes = new byte[buf.readableBytes()];
            buf.readBytes(resBytes);
            String res = new String(resBytes, "UTF-8");
            System.out.println("res : " + res);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            System.out.println("exceptionCaught");
            ctx.close();
        }
    }
}
