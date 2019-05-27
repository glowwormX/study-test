package iText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author 徐其伟
 * @Description:
 * @date 19-3-11 下午2:53
 */
public class Test {
    public static void main(String[] args) throws FileNotFoundException {
        PdfService pdfService = new PdfService();
//        String timeFile = System.currentTimeMillis() + "";
        String tempFile = PdfService.RESOURCE_PREFIX_INDEX + "/";
        createDirs(tempFile);
        // 添加字体,这一步很关键，否则中文会显示不出来
        pdfService.copyFont(tempFile);
        FileInputStream inputStream = new FileInputStream("/home/hlkj/html/test/mb.3.html");
        File pdfFile = createFlawPdfFile(tempFile, "demo");
        long l1 = System.currentTimeMillis();
        pdfService.createPdfFromHtml(pdfFile.getName(), inputStream, tempFile);
        long l2 = System.currentTimeMillis();
        System.out.println(l2 - l1);
    }
    /**
     * 新建文件夹
     * @param dirsPath
     */
    private static void createDirs(String dirsPath) {
        File temFile = new File(dirsPath);
        if (!temFile.exists()) {
            temFile.mkdirs();
        }
    }
    /**
     * 创建漏洞pdf版本空文件
     * @param fileDir
     * @param fileName
     * @return
     */
    private static File createFlawPdfFile(String fileDir, String fileName) {
        File tempFile;
        do {
            tempFile = new File(fileDir + fileName + ".pdf");
        } while (tempFile.exists());
        return tempFile;
    }
}
