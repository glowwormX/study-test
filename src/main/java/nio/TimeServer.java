package nio;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @author 徐其伟
 * @Description:
 * @date 19-2-18 下午3:17
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        new Thread(new MultiplexerTimeServer(port)).run();
    }

    static class MultiplexerTimeServer implements Runnable {
        private Selector selector;
        private ServerSocketChannel serverSocketChannel;
        private volatile boolean stop;
        private String str;
        private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

        public MultiplexerTimeServer(int port) {
            try {
                selector = Selector.open();
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        public void stop() {
            this.stop = true;
        }

        public void run() {
            while (!stop) {
                try {
                    int num = selector.select(1000);
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iterator = selectionKeys.iterator();
                    SelectionKey key = null;
                    while (iterator.hasNext()) {
                        key = iterator.next();
                        iterator.remove();

                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isAcceptable()) {
                            accept(key);
                        } else if (key.isReadable()) {
                            read(key);
                        } else if (key.isWritable()) {
                            write(key);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void accept(SelectionKey key) throws IOException {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel sc = ssc.accept();
            sc.configureBlocking(false);
            sc.register(selector, SelectionKey.OP_READ);
            System.out.println("a new client connected " + sc.getLocalAddress());
        }

        private void read(SelectionKey key) throws IOException {
            SocketChannel sc = (SocketChannel) key.channel();
            int readBytes;
            try {
                readBytes = sc.read(readBuffer);
            } catch (IOException e){
                key.cancel();
                sc.close();
                return;
            }
            if(readBytes > 0) {
                readBuffer.clear();
                String body = new String(readBuffer.array(),"UTF-8");
                System.out.println("The time server receive order : " + body);
                str = new Date(System.currentTimeMillis()).toString();
                sc.register(selector, SelectionKey.OP_WRITE);
            }
        }

        private void write(SelectionKey key) throws IOException, ClosedChannelException {
            SocketChannel channel = (SocketChannel) key.channel();
            System.out.println("write:"+str);

            sendBuffer.clear();
            sendBuffer.put(str.getBytes());
            sendBuffer.flip();
            channel.write(sendBuffer);
            channel.register(selector, SelectionKey.OP_READ);
        }
    }
}
