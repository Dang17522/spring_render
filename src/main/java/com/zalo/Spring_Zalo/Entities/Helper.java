package com.zalo.Spring_Zalo.Entities;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Helper {
    public static String[] HEADER = {
            "id",
            "customerName",
            "rewardName",
            "Event Name",
            "Status",
            "exchangeRewardDate"

    };

    public static String[] HEADER_CUSTOMER = {
            "Id",
            "Name",
            "Phone Number",
            "Address",
            "Event Name",
            "Point"

    };
    public static String SHEET_NAME="data";
    public static ByteArrayInputStream dataToExcel(List<CustomerReward> list) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Sheet sheet = workbook.createSheet(SHEET_NAME);

            Row row = sheet.createRow(0);
            for (int i=0;i<HEADER.length;i++){
                Cell cell = row.createCell(i);
                cell.setCellValue(HEADER[i]);
            }
            for (int i=0;i< list.size();i++){
                Row dataRow = sheet.createRow(i+1);
                dataRow.createCell(0).setCellValue(list.get(i).getCustomer().getId());
                dataRow.createCell(1).setCellValue(list.get(i).getCustomer().getName());
                dataRow.createCell(2).setCellValue(list.get(i).getReward().getName());
                dataRow.createCell(3).setCellValue(list.get(i).getEvent().getName());
                dataRow.createCell(4).setCellValue(list.get(i).getStatus());
                dataRow.createCell(5).setCellValue(list.get(i).getExchangeRewardDate().format(formatter));
            }
            workbook.write(out);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workbook.close();
            out.close();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    public static ByteArrayInputStream dataToExcelByCustomer(List<Object[]> list) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            Sheet sheet = workbook.createSheet(SHEET_NAME);

            Row row = sheet.createRow(0);
            for (int i=0;i<HEADER_CUSTOMER.length;i++){
                Cell cell = row.createCell(i);
                cell.setCellValue(HEADER_CUSTOMER[i]);
            }
            int i = 0;
            for (Object[] objects : list){
                i++;
                Row dataRow = sheet.createRow(i);
                dataRow.createCell(0).setCellValue((Integer) objects[0]);
                dataRow.createCell(1).setCellValue((String) objects[1]);
                dataRow.createCell(2).setCellValue((String) objects[2]);
                dataRow.createCell(3).setCellValue((String) objects[3]);
                dataRow.createCell(4).setCellValue((String) objects[4]);
                dataRow.createCell(5).setCellValue(objects[5] != null ? String.valueOf(objects[5]) : "0");
            }
            workbook.write(out);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            workbook.close();
            out.close();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

}
