package nio.netty.stick;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Date;

/**粘包
 * @author 徐其伟
 * @Description:
 * @date 19-2-19 下午3:51
 */
public class TimeServer {
    public static void main(String[] args) {
        new TimeServer().bind(8080);
    }

    public void bind(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap sbs = new ServerBootstrap();
            sbs.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChildChannnelHandler());
            //绑定端口，同步等待成功
            ChannelFuture cf = sbs.bind(port).sync();

            //等待服务端监听端口关闭
            cf.channel().closeFuture().sync();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            //释放线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ChildChannnelHandler extends ChannelInitializer<SocketChannel> {
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new TimeServerHandler());
        }

        private class TimeServerHandler extends ChannelHandlerAdapter {
            /*********/
            private int count;
            /*********/

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf buf = (ByteBuf) msg;
                byte[] req = new byte[buf.readableBytes()];
                buf.readBytes(req);
                String body = new String(req, "UTF-8");
                /*********/
                System.out.println("body : " + body + "count : " + ++count);
                /*********/

                String res = new Date(System.currentTimeMillis()).toString();
                ByteBuf resBytes = Unpooled.copiedBuffer(res.getBytes());
                ctx.write(resBytes);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                ctx.close();
            }

            @Override
            public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                ctx.flush();
            }
        }
    }
}
