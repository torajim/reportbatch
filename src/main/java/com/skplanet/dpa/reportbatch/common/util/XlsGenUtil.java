package com.skplanet.dpa.reportbatch.common.util;

import com.skplanet.dpa.reportbatch.domain.dmfc.repository.MdProduct;
import com.skplanet.dpa.reportbatch.domain.dmfc.repository.MdSoRate;
import com.skplanet.dpa.reportbatch.domain.dmfc.repository.SoPrediction;
import com.skplanet.dpa.reportbatch.domain.dmfc.repository.SoStatus;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XlsGenUtil {
    private CellStyle int_cs;
    private CellStyle float_cs;
    private CellStyle String_body_cs;
    private CellStyle String_header_cs;

    public Workbook createExcel(List<String> headers, List<Object> values, Workbook wb, String sheetName){
        if(wb == null) {
            wb = new HSSFWorkbook();
        }
        Sheet s = wb.createSheet();
        Row r = null;
        Cell c = null;

        int_cs = wb.createCellStyle();
        float_cs = wb.createCellStyle();
        String_body_cs = wb.createCellStyle();
        String_header_cs = wb.createCellStyle();
        DataFormat df = wb.createDataFormat();

        Font bold_black_f = wb.createFont();
        Font normal_black_f = wb.createFont();
        //Font normal_red_f = wb.createFont();

        bold_black_f.setFontHeightInPoints((short)10);
        bold_black_f.setColor(IndexedColors.BLACK.index);
        bold_black_f.setBold(true);

        normal_black_f.setFontHeightInPoints((short)10);
        normal_black_f.setColor(IndexedColors.BLACK.index);
        normal_black_f.setBold(false);

        //normal_red_f.setFontHeightInPoints((short)10);
        //normal_red_f.setColor(IndexedColors.RED.index);
        //normal_red_f.setBold(false);

        String_header_cs.setFont(bold_black_f);
        String_header_cs.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
        String_header_cs = setRectangleBorder(String_header_cs);

        int_cs.setFont(normal_black_f);
        int_cs.setDataFormat(df.getFormat("#,##0"));
        int_cs = setRectangleBorder(int_cs);

        float_cs.setFont(normal_black_f);
        float_cs.setDataFormat(df.getFormat("#,##0.00"));
        float_cs = setRectangleBorder(float_cs);

        String_body_cs.setFont(normal_black_f);
        String_body_cs.setDataFormat(HSSFDataFormat.getBuiltinFormat("text"));
        String_body_cs = setRectangleBorder(String_body_cs);

        wb.setSheetName(wb.getNumberOfSheets()-1, sheetName);

        // Set Column Width
        // Unicode 25 * 20 per one characters, number 12.5 * 20

        // Headers
        int rowIndex = 0;
        r = s.createRow(rowIndex);
        r.setHeight((short)(13 * 20));

        int colIndex = 0;
        for(String headerCol : headers){
            c = r.createCell(colIndex++);
            c.setCellValue(headerCol);
            c.setCellStyle(String_header_cs);
        }

        // Bodies
        for(Object tempRow : values){
            rowIndex++;
            r = s.createRow(rowIndex);
            r.setHeight((short)(13 * 20));

            int i = 0;
            if(tempRow instanceof MdSoRate){
                setCellValueNStyle(((MdSoRate) tempRow).getMdName(), r.createCell(i++));
                setCellValueNStyle(((MdSoRate) tempRow).getSellCnt(), r.createCell(i++));
                setCellValueNStyle(((MdSoRate) tempRow).getSoCnt(), r.createCell(i++));
                setCellValueNStyle(((MdSoRate) tempRow).getSoRate(), r.createCell(i++));
            }else if(tempRow instanceof SoStatus){
                setCellValueNStyle(((SoStatus) tempRow).getPrdNo() + "", r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getStckNo() + "", r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getPrdName(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getMdName(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getStckQty(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getOrdrQty(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getWaitStckQty(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getWkSelQty1(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getWkSelQty2(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getWkSelQty4(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getSelStat(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getDisplayYn(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getDstStckStat(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getDlvClf(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getSoPeriod(), r.createCell(i++));
                setCellValueNStyle(((SoStatus) tempRow).getCenterName(), r.createCell(i++));
            }else if(tempRow instanceof SoPrediction){
                setCellValueNStyle(((SoPrediction) tempRow).getPrdNo() + "", r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getStckNo() + "", r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getPrdName(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getMdName(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getStckQty(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getOrdrQty(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getWaitStckQty(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getTotalStckQty(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getWkPreQty(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getWkSelQty1(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getWkSelQty2(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getWkSelQty4(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getSelStat(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getDstStckStat(), r.createCell(i++));
                setCellValueNStyle(((SoPrediction) tempRow).getDlvClf(), r.createCell(i++));
            }else if(tempRow instanceof MdProduct){
                setCellValueNStyle(((MdProduct) tempRow).getMdName(), r.createCell(i++));
                setCellValueNStyle(((MdProduct) tempRow).getPrdNo() + "", r.createCell(i++));
                setCellValueNStyle(((MdProduct) tempRow).getPrdName(), r.createCell(i++));
            }
        }

        // Auto Column Width
        for(int sheetNum = 0; sheetNum < wb.getNumberOfSheets(); sheetNum++) {
            Sheet tempSheet = wb.getSheetAt(sheetNum);
            for (int rowNum = 0; rowNum < tempSheet.getLastRowNum(); rowNum++) {
                Row tempRow = tempSheet.getRow(rowNum);
                for (int colNum = 0; colNum < tempRow.getLastCellNum(); colNum++) {
                    tempSheet.autoSizeColumn(colNum);
                }
            }
        }

        return wb;
    }

    private Cell setCellValueNStyle(Object value, Cell c){
        if(value instanceof String){
            c.setCellValue(value + "");
            c.setCellStyle(String_body_cs);
        }else if(value instanceof Float) {
            c.setCellValue((Float)value);
            c.setCellStyle(float_cs);
        }else if(value instanceof Double){
            c.setCellValue((Double)value);
            c.setCellStyle(float_cs);
        }else if(value instanceof Integer){
            c.setCellValue((Integer)value);
            c.setCellStyle(int_cs);
        }else if(value instanceof Long){
            c.setCellValue((Long)value);
            c.setCellStyle(int_cs);
        }
        return c;
    }

    public void createExcel(List<String> headers, List<Object> values, String fileName) throws IOException {
        FileOutputStream out = new FileOutputStream(fileName + ".xls");
        Workbook wb = createExcel(headers, values, null, fileName);
        wb.write(out);
        out.close();
    }

    public void makeFileWithWorkbook(String fileName, Workbook wb) throws IOException {
        FileOutputStream out = new FileOutputStream(fileName + ".xls");
        wb.write(out);
        out.close();
    }

    private CellStyle setRectangleBorder(CellStyle cs){
        cs.setBorderBottom(BorderStyle.THIN);
        cs.setBorderTop(BorderStyle.THIN);
        cs.setBorderLeft(BorderStyle.THIN);
        cs.setBorderRight(BorderStyle.THIN);
        return cs;
    }

    public static void main(String[] args) {
        List<MdProduct> values = new ArrayList<>();
        List<String> headers_pic = new ArrayList<String>(Arrays.asList(new String[]{"담당MD", "상품번호", "상품명"}));
        MdProduct a = new MdProduct("장우혁", 123, "상품A");
        MdProduct b = new MdProduct("장우혁", 456, "상품B");
        MdProduct c = new MdProduct("장우혁", 789, "상품C");
        values.add(a);
        values.add(b);
        values.add(c);
        XlsGenUtil gen = new XlsGenUtil();
        Workbook b1 = gen.createExcel(headers_pic, Arrays.asList(values.toArray()), null, "담당자상품1");
        Workbook b2 = gen.createExcel(headers_pic, Arrays.asList(values.toArray()), b1, "담당자상품2");

        String fileName = "test_sold_out";
        try {
            gen.makeFileWithWorkbook(fileName, b2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}