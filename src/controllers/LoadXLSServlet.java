package controllers;

import jxl.Sheet;
import jxl.Workbook;
import jxl.write.WritableWorkbook;
import jxl.SheetSettings;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class LoadXLSServlet extends HttpServlet {

    private static final int CR = (int) '\r';
    private static final int LF = (int) '\n';
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        byte[] dataSlice = extractData(request);
        ByteArrayInputStream is= new ByteArrayInputStream(dataSlice);
        Workbook wb=null;
        try {
            wb= Workbook.getWorkbook(is);
            is.close();
         //   wb.close();

        }catch (Exception e){
            System.out.println("Error"+e );
        }

        ByteArrayOutputStream os= new ByteArrayOutputStream();
        WritableWorkbook  wrtWorkbook = Workbook.createWorkbook(os,wb);

      try {
          wrtWorkbook.write();
      }catch (Exception e){
          System.out.println(e);
      }

        try {
            wrtWorkbook.close();
        }catch (Exception e){
            System.out.println(e);
    }
        response.setHeader("Cache-Control", "max-age=30");
        response.setContentType("application / vnd.ms - excel");
        response.setHeader("Content-disposition","inline; filename=myfileJXLWRT.xls");
        ServletOutputStream sos = response.getOutputStream();
        // FileInputStream fio = new FileInputStream("./" + "myfileJXLWRT.xls");
        os.flush();
        os.close();

       ByteArrayInputStream inputStream = new ByteArrayInputStream(os.toByteArray());

        System.out.print("inputStream= ");
        int c;
        while(( c = inputStream.read()) != -1) {
            sos.write(c);
         //   System.out.print(c);
        }
        sos.close();
        inputStream.close();
    }

    private byte[] extractData(HttpServletRequest request) throws IOException {
        // Содержимое пришедших байтов их запроса (содержимое приходящего файла)
        InputStream is = request.getInputStream();
        int[] data = new int[request.getContentLength()];
        int bytes;
        int counter = 0;
        while((bytes=is.read())!=-1) {
            data[counter]=bytes;
            counter++;
        }
        is.close();
        // Определение индексов срезки
        int i;
        int beginSliceIndex = 0;
        // Конечный индекс срезки - длина границы + доп. символы.
        int endSliceIndex = data.length - getBoundary(request).length()-9;

        for(i = 0; i < data.length; i++) {
            // Начальный индекс срезки: после того как встретятся 2 раза подряд \r\n
            if(data[i] == CR && data[i+1] == LF && data[i+2] == CR && data[i+3] == LF){
                beginSliceIndex = i+4;
                break;
            }
        }
        byte[] dataSlice = new byte[endSliceIndex-beginSliceIndex+1];
        for(i = beginSliceIndex; i<=endSliceIndex; i++) {
            dataSlice[i-beginSliceIndex]=(byte)data[i];
        }
        return dataSlice;
    }
    // Поиск границы
    private String getBoundary(HttpServletRequest request) {
        String cType = request.getContentType();
        return cType.substring(cType.indexOf("boundary=")+9);
    }
}
