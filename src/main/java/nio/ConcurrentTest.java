package nio;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

/** 测试类tomcat7、8  来源：视频
 * @author 徐其伟
 * @Description:
 * @date 2019/2/14 15:39
 */
public class ConcurrentTest {
    static CountDownLatch cdl = new CountDownLatch(20);
    static class ClientThread implements Runnable{
        public void run() {
            try {
                cdl.await();
                Socket socket = new Socket("localhost", 8080);
                System.out.println(Thread.currentThread().getName() + " working...");
                Thread.sleep(10*60*1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            ClientThread ct = new ClientThread();
            new Thread(ct).start();
            cdl.countDown();
        }
    }
}
