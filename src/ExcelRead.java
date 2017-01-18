/**
 * Created by ianagez on 18.01.17.
 */


import java.io.File;
import java.io.IOException;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ExcelRead  {

    private String inputFile;

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String read() throws IOException  {
        StringBuilder s = new StringBuilder();
        String r;
        File inputWorkbook = new File(inputFile);
        Workbook w;
        try {
            w = Workbook.getWorkbook(inputWorkbook);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            // Loop over first 10 column and lines

            for (int j = 0; j < sheet.getColumns(); j++) {
                s.append("<br>");
                for (int i = 0; i < sheet.getRows(); i++) {
                    Cell cell = sheet.getCell(j, i);
                    CellType type = cell.getType();
                    if (type == CellType.LABEL) {
                        s.append(cell.getContents()+" ");
//                        System.out.println("I got a label "
//                                + cell.getContents());
                    }

                    if (type == CellType.NUMBER) {
                        s.append(cell.getContents()+" ");
//                        System.out.println("I got a number "
//                                + cell.getContents());
                    }
                    if (type == CellType.DATE) {
                        s.append(cell.getContents()+" ");
//                        System.out.println("I got a number "
//                                + cell.getContents());
                    }
                }
            }
        } catch (BiffException e) {
            e.printStackTrace();
        }
        r=s.toString();
        return r;
    }

    public static void main(String[] args) throws IOException {
//        ExcelRead test = new ExcelRead();
//        test.setInputFile("/home/ianagez/lars.xls");
//        test.read();
    }

}

