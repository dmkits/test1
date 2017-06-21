package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class UploadServlet extends HttpServlet
{
    private static final String CONTENT_TYPE = "text/html";
    private static final int CR = (int)'\r';
    private static final int LF = (int)'\n';

    private static final String DATA_LIST = "Dati";
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

//Поток, в который будет писаться содержимое (в принципе может быть любой OutputStream)
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("/home/ianagez/myfile.xls");
            int[] dataSlice = extractData(request);
            int i;
            for(i=0;i<dataSlice.length; i++ ) fos.write(dataSlice[i]);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            request.getRequestDispatcher("/pages/upload_xls_parse_error.html").forward(request, response);
        }

        ExcelRead test = new ExcelRead();
        test.setInputFile("/home/ianagez/myfile.xls");
        HashMap<String, Object> outData=test.createHashData(DATA_LIST,DATA_COLUMNS);
        //String res = null;
        try {
            outData =test.createHashData(DATA_LIST, DATA_COLUMNS);
           // res = test.createHashData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //test.read();
//        ArrayList<HashMap<String,Object>> columns= new ArrayList<>();
//        ArrayList<HashMap<String,Object>> data= new ArrayList<>();
//
//        HashMap outData= new HashMap();
//        outData.put("columns",columns);
//        outData.put("data",data);

       // Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ").create();
       // String json = gson.toJson(outData);
        String json = (new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ").create()).toJson(outData);

        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<body>");
        out.println(json);


        out.println("</body>");
        out.println("</html>");


        // HTML форма отправляемая методом post
        response.setContentType(CONTENT_TYPE);

        RequestDispatcher view = request.getRequestDispatcher("/pages/upload_servlet_post.html");
        view.forward(request, response);
    }
    private int[] extractData(HttpServletRequest request) throws IOException {
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

        int[] dataSlice = new int[endSliceIndex-beginSliceIndex+1];
        for(i = beginSliceIndex; i<=endSliceIndex; i++) {
            dataSlice[i-beginSliceIndex]=data[i];
        }
        return dataSlice;
    }

    // Поиск границы
    private String getBoundary(HttpServletRequest request) {
        String cType = request.getContentType();
        return cType.substring(cType.indexOf("boundary=")+9);
    }
}
