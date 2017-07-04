package controllers; /**
 * Created by ianagez on 18.01.17.
 */


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import jxl.*;

public class ExcelRead  {

    public static HashMap getHashData(InputStream is, String sheetTitle, String[] columnsTitle) throws IOException  {
        ArrayList<HashMap<String,Object>> columns= new ArrayList<>();
        ArrayList<HashMap<String,Object>> data= new ArrayList<>();
        HashMap<String, Object> outData= new HashMap<String, Object>();
        outData.put("columns",columns);
        outData.put("data",data);

        Workbook w;
        try {
            w = Workbook.getWorkbook(is);
            Sheet sheet=w.getSheet(0);
            if(sheet==null){
                return  outData;
            }
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
                    String key= sheet.getCell(columnNum, 0).getContents();
                    cell = sheet.getCell(columnNum, i);
                    rowData.put(key, getExcelCellValue(cell));
                }
               for(HashMap.Entry<String, Object> entry : rowData.entrySet()) {
                    if(entry.getValue()!=null){
                        data.add(rowData);
                    };
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outData;
    }
    private static Object getExcelCellValue(Cell cell){
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

