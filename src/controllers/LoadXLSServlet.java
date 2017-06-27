package controllers;


import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import static jxl.Workbook.createWorkbook;


public class LoadXLSServlet extends HttpServlet {

    private static final int CR = (int) '\r';
    private static final int LF = (int) '\n';

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream("/home/ianagez/myfileJXL.xls");
            int[] dataSlice = extractData(request);
            int i;
            for (i = 0; i < dataSlice.length; i++) fos.write(dataSlice[i]);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            request.getRequestDispatcher("/pages/upload_xls_parse_error.html").forward(request, response);
        }

        File f = new File("/home/ianagez/myfileJXL.xls");
        Workbook wb=null;
        try {
            wb= Workbook.getWorkbook(f);

        }catch (Exception e){
            System.out.println("Error"+e );
        }

        WritableWorkbook  wrtWorkbook = Workbook.createWorkbook(new File("myfileJXLWRT.xls"),wb);
        wrtWorkbook.write();
        try {
            wrtWorkbook.close();
        }catch (Exception e){
            System.out.println(e);
    }
        response.setHeader("Cache-Control", "max-age=30");
        response.setContentType("application / vnd.ms - excel");
        response.setHeader("Content-disposition","inline; filename=myfileJXLWRT.xls");

        ServletOutputStream sos = response.getOutputStream();
        FileInputStream fio = new FileInputStream("./" + "myfileJXLWRT.xls");

        int c;
        while(( c = fio.read()) != -1) {
            sos.write(c);
        }
        sos.close();
    }

    private int[] extractData(HttpServletRequest request) throws IOException {
        // Содержимое пришедших байтов их запроса (содержимое приходящего файла)
        InputStream is = request.getInputStream();
        int[] data = new int[request.getContentLength()];
        int bytes;
        int counter = 0;
        while ((bytes = is.read()) != -1) {
            data[counter] = bytes;
            counter++;
        }
        is.close();
        // Определение индексов срезки
        int i;
        int beginSliceIndex = 0;
        // Конечный индекс срезки - длина границы + доп. символы.
        int endSliceIndex = data.length - getBoundary(request).length() - 9;

        for (i = 0; i < data.length; i++) {
            // Начальный индекс срезки: после того как встретятся 2 раза подряд \r\n
            if (data[i] == CR && data[i + 1] == LF && data[i + 2] == CR && data[i + 3] == LF) {
                beginSliceIndex = i + 4;
                break;
            }
        }
        int[] dataSlice = new int[endSliceIndex - beginSliceIndex + 1];
        for (i = beginSliceIndex; i <= endSliceIndex; i++) {
            dataSlice[i - beginSliceIndex] = data[i];
        }
        return dataSlice;
    }
    private String getBoundary(HttpServletRequest request) {
        String cType = request.getContentType();
        return cType.substring(cType.indexOf("boundary=") + 9);
    }
}

