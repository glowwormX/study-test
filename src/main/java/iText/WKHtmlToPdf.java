package iText;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;
import sun.misc.IOUtils;

import javax.annotation.Resource;
import java.io.*;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 徐其伟
 * @Description:
 * @date 19-3-11 下午12:34
 */
public class WKHtmlToPdf {
    //wkhtmltopdf在系统中的路径
    private static final String toPdfTool = "/usr/local/bin/wkhtmltopdf";

    /**
     * html转pdf
     *
     * @param srcPath  html路径，可以是硬盘上的路径，也可以是网络路径
     * @param destPath pdf保存路径
     * @return 转换成功返回true
     */
    public static boolean convert(String srcPath, String destPath) {
        File file = new File(destPath);
        File parent = file.getParentFile();
        //如果pdf保存路径不存在，则创建路径
        if (!parent.exists()) {
            parent.mkdirs();
        }
        StringBuilder cmd = new StringBuilder();
        if (System.getProperty("os.name").indexOf("Windows") == -1) {
            //非windows 系统
//            toPdfTool = FileUtil.convertSystemFilePath("/home/ubuntu/wkhtmltox/bin/wkhtmltopdf");
        }
        cmd.append(toPdfTool);
        cmd.append(" ");
//        cmd.append("  --header-line");//页眉下面的线
//        //cmd.append("  --header-center 这里是页眉这里是页眉这里是页眉这里是页眉 ");//页眉中间内容
//        cmd.append("  --margin-top 3cm ");//设置页面上边距 (default 10mm)
//        cmd.append(" --header-html  file:///" + WebUtil.getServletContext().getRealPath("") + FileUtil.convertSystemFilePath("\\style\\pdf\\head.html"));// (添加一个HTML页眉,后面是网址)
//        cmd.append(" --header-spacing 5 ");//    (设置页眉和内容的距离,默认0)
//        //cmd.append(" --footer-center (设置在中心位置的页脚内容)");//设置在中心位置的页脚内容
//        cmd.append(" --footer-html  file:///" + WebUtil.getServletContext().getRealPath("") + FileUtil.convertSystemFilePath("\\style\\pdf\\foter.html"));// (添加一个HTML页脚,后面是网址)
//        cmd.append(" --footer-line");//* 显示一条线在页脚内容上)
//        cmd.append(" --footer-spacing 5 ");// (设置页脚和内容的距离)
        cmd.append(srcPath);
        cmd.append(" ");
        cmd.append(destPath);


        boolean result = true;
        try {
            Process proc = Runtime.getRuntime().exec(cmd.toString());
            HtmlToPdfInterceptor error = new HtmlToPdfInterceptor(proc.getErrorStream());
            HtmlToPdfInterceptor output = new HtmlToPdfInterceptor(proc.getInputStream());
            error.start();
            output.start();
            proc.waitFor();
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }

        return result;
    }

    public static void main(String[] args) {
        WKHtmlToPdf.convert("http://192.168.1.77/test/%E6%B0%91%E7%94%A8%E7%88%86%E7%82%B8%E7%89%A9%E5%93%81%E6%9C%AB%E7%AB%AF%E7%AE%A1%E6%8E%A7%E5%B9%B3%E5%8F%B0.html", "/data/test.pdf");
    }
}
class HtmlToPdfInterceptor extends Thread {
    private InputStream is;

    public HtmlToPdfInterceptor(InputStream is){
        this.is = is;
    }

    public void run(){
        try{
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ((line = br.readLine()) != null) {
                System.out.println(line.toString()); //输出内容
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}