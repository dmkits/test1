package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.Label;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadServlet extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html";
    //private static final String CONTENT_TYPE = "application/vnd.ms-excel";
    private static final int CR = (int)'\r';
    private static final int LF = (int)'\n';

    private static final String DATA_LIST = "Лист 1";
    private static final String[] DATA_COLUMNS = new String[]{"Gender","Cat.","Sub.","Item No.","Q.ty", "Gross Price by unit", "Net Amount"};

    public void init(ServletConfig config) throws ServletException
    {
        super.init(config);
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
       response.setContentType(CONTENT_TYPE);
        RequestDispatcher view = request.getRequestDispatcher("/pages/upload_servlet_get.html");
        view.forward(request, response);
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

      //  System.out.println("request="+request);

       //Поток, в который будет писаться содержимое (в принципе может быть любой OutputStream)
       // System.out.println("request="+request.toString());
        byte[] dataSlice = extractData(request);
        HashMap<String, Object> outData;
        try {
            InputStream is = new ByteArrayInputStream(dataSlice);

//            int i;
//            for(i=0;i<dataSlice.length; i++ ) is.write(dataSlice[i]);
//            is.flush();
//            os.close();
            outData =ExcelRead.getHashData(is, DATA_LIST, DATA_COLUMNS);
            String json = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ").create()).toJson(outData);
            PrintWriter out = response.getWriter();
            out.println("<html>");
            out.println("<body>");
            out.println(json);
            out.println("</body>");
            out.println("</html>");
        } catch (Exception e) {
            request.getRequestDispatcher("/pages/upload_xls_parse_error.html").forward(request, response);
        }
        // HTML форма отправляемая методом post
        response.setContentType(CONTENT_TYPE);
//        response.setHeader("Content-Disposition",
//                "attachment; filename=sampleName.xls");
//
//        WritableWorkbook w = Workbook.createWorkbook(response.getOutputStream());
//        WritableSheet s = w.createSheet("Demo", 0);

       // s.addCell(new Label(0, 0, "Hello World"));
//        w.write();
       // w.close();

        //RequestDispatcher view = request.getRequestDispatcher("/pages/upload_servlet_post.html");
        //view.forward(request, response);
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
