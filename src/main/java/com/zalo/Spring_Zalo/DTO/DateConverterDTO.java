package com.zalo.Spring_Zalo.DTO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateConverterDTO {

    private static final DateTimeFormatter[] FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("yyyy 年 M 月 d 日", Locale.JAPAN),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyyMMdd"),
            DateTimeFormatter.ofPattern("dd MMMM yyyy"),
            DateTimeFormatter.ofPattern("MMMM dd, yyyy"),
            
    };

    public static LocalDate convertToDate(String input) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                return LocalDate.parse(input, formatter);
            } catch (Exception e) {
                // Log thông báo lỗi hoặc xử lý lỗi tại đây
                System.err.println("Lỗi chuyển đổi định dạng: " + e.getMessage());
            }
        }
        return null;
    }
}
