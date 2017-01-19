/**
 * Created by ianagez on 18.01.17.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.*;
import jxl.read.biff.BiffException;

public class ExcelRead  {

    private String inputFile;

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

//    public String read() throws IOException  {
//        StringBuilder s = new StringBuilder();
//        String r;
//        File inputWorkbook = new File(inputFile);
//        Workbook w;
//        try {
//            w = Workbook.getWorkbook(inputWorkbook);
//            // Get the first sheet
//            Sheet sheet = w.getSheet(0);
//            // Loop over first 10 column and lines
//
//            for (int j = 0; j < sheet.getColumns(); j++) {
//                s.append("<br>");
//                for (int i = 0; i < sheet.getRows(); i++) {
//                    Cell cell = sheet.getCell(j, i);
//                    CellType type = cell.getType();
//                    if (type == CellType.LABEL) {
//                        s.append(cell.getContents()+" ");
////                        System.out.println("I got a label "
////                                + cell.getContents());
//                    }
//
//                    if (type == CellType.NUMBER) {
//                        s.append(cell.getContents()+" ");
////                        System.out.println("I got a number "
////                                + cell.getContents());
//                    }
//                    if (type == CellType.DATE) {
//                        s.append(cell.getContents()+" ");
////                        System.out.println("I got a number "
////                                + cell.getContents());
//                    }
//                }
//            }
//        } catch (BiffException e) {
//            e.printStackTrace();
//        }
//        r=s.toString();
//        return r;
//    }


    public HashMap createHashData(String sheetTitle, String[] columnsTitle) throws IOException  {

        ArrayList<HashMap<String,Object>> columns= new ArrayList<>();
        ArrayList<HashMap<String,Object>> data= new ArrayList<>();

        HashMap<String, Object> outData= new HashMap<String, Object>();
        outData.put("columns",columns);
        outData.put("data",data);

        File inputWorkbook = new File(inputFile);
        Workbook w;
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
           // Sheet sheet = w.getSheet(3);
            // Loop over first 10 column and lines


//            Cell cell;
//            for (int j = 0; j < sheet.getColumns(); j++){
//                HashMap <String,Object> columnNames=new HashMap<String,Object> ();
//                cell=sheet.getCell(j, 0);
//                columnNames.put("title", cell.getContents());
//                columns.add(columnNames);
//            }
//            for (int i = 1; i < sheet.getRows(); i++) {
//                HashMap <String,Object> rowData = new HashMap<String,Object> ();
//
//                for (int j = 0; j < sheet.getColumns(); j++) {
//                    String key= sheet.getCell(j, 0).getContents();
//                    cell = sheet.getCell(j, i);
//                    rowData.put(key, getExcelCellValue(cell));
//                }
//                data.add(rowData);
//            }
//        } catch (BiffException e) {
//            e.printStackTrace();
//        }
//        return outData;

            Sheet sheet=null;
            for (int i = 0; i < w.getSheets().length; i++) {
                if (w.getSheet(i).getName().equalsIgnoreCase(sheetTitle)){
                    sheet=w.getSheet(i);
                    break;
                }
            }
            Cell cell;
            ArrayList<Integer> columnNumbersList=new ArrayList<>();
            for (int j = 0; j < sheet.getColumns()-1; j++){
                HashMap <String,Object> columnNames=new HashMap<String,Object> ();
                cell=sheet.getCell(j, 0);
                String cellContent=cell.getContents();
                for(int i=0; i<columnsTitle.length;i++){
                    if(cellContent.equalsIgnoreCase(columnsTitle[i])){
                        columnNames.put("title", cellContent);
                        columns.add(columnNames);
                        columnNumbersList.add(j);
                    }
                }
            }
            for (int i = 1; i < sheet.getRows(); i++) {
                HashMap <String,Object> rowData = new HashMap<String,Object> ();
                for (int j = 0; j < columnNumbersList.size(); j++) {
                    int columnNum=columnNumbersList.get(j);
                    String key= sheet.getCell(columnNum, 0).getContents().trim();
                    cell = sheet.getCell(columnNum, i);
                    rowData.put(key, getExcelCellValue(cell));
                }
               for(HashMap.Entry<String, Object> entry : rowData.entrySet()) {
                    if(entry.getValue()!=null){
                        data.add(rowData);
                    };
                }
            }
        } catch (BiffException e) {
            e.printStackTrace();
        }
        return outData;
    }
    private Object getExcelCellValue(Cell cell){
        CellType type = cell.getType();
        if (type == CellType.LABEL) {
            return cell.getContents().trim();
        }
        if (type == CellType.NUMBER) {
            return ((NumberCell)cell).getValue();
        }
        if (type == CellType.DATE) {
            return ((DateCell)cell).getDate();
        }
        return null;
    }
}

