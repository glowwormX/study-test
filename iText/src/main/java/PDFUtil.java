import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/** * pdf工具类 */
public class PDFUtil {

    public static void htmlToPDF(String htmlString, String pdfPath) {
        URL url = null;
        InputStream in = null;
        try {
            url = new URL("https://www.baidu.com/");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // 创建一个document对象实例
            Document document = new Document();
            // 为该Document创建一个Writer实例
            PdfWriter pdfwriter = PdfWriter.getInstance(document,
                    new FileOutputStream(pdfPath));
            pdfwriter.setViewerPreferences(PdfWriter.HideToolbar);
            // 打开当前的document
            document.open();
            InputStreamReader isr = new InputStreamReader(in, "UTF-8");
            XMLWorkerHelper.getInstance().parseXHtml(pdfwriter, document,in,null,null,new MyFontsProvider());
            //XMLWorkerHelper.getInstance().p
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        PDFUtil.htmlToPDF("/data/aa.html", "/data/a.pdf");
    }
}

/** * 处理中文不显示和乱码问题 */
class MyFontsProvider extends XMLWorkerFontProvider {

    public MyFontsProvider(){
        super(null, null);
    }

    @Override
    public Font getFont(final String fontname, String encoding, float size, final int style) {
        String fntname = fontname;
        if (fntname == null) {
            fntname = "宋体";//windows下
            //fntname = "fontFile/simsun.ttf";//linux系统下
        }
        if (size == 0) {
            size = 4;
        }
        return super.getFont(fntname, encoding, size, style);
    }
}