package com.zalo.Spring_Zalo.request;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;
import java.util.UUID;
public class FileStorageManager {
    private String storageDirectory;
      private static final String STORAGE_DIRECTORY;

    static {
        ResourceBundle bundle = ResourceBundle.getBundle("application");
        STORAGE_DIRECTORY = bundle.getString("Storge_Directory");
    }
    public FileStorageManager(String storageDirectory) {
        this.storageDirectory = storageDirectory;
    }
    public String storeFile(MultipartFile file, int eventId, int customerId, LocalDateTime createDate) throws IOException {
        String fileName = generateFileName(eventId, customerId, createDate);
        Path filePath = Paths.get(storageDirectory, fileName);
        Files.write(filePath, file.getBytes());
        return fileName;
    }
    public static String getStorageDirectory() {
        return STORAGE_DIRECTORY;
    }
    public String generateCodeName(int eventId, int customerId) {
        return String.format("eventID:%d_customerID:%d", eventId, customerId);
    }
    private String generateFileName(int eventId, int customerId, LocalDateTime createDate) {
        // Làm tròn giây xuống đến 1 chữ số thập phân
        LocalDateTime roundedCreateDate = createDate.truncatedTo(ChronoUnit.SECONDS);
        // Lấy giá trị năm, tháng, ngày, giờ, phút và giây từ LocalDateTime
        int year = roundedCreateDate.getYear();
        int month = roundedCreateDate.getMonthValue();
        int day = roundedCreateDate.getDayOfMonth();
        int hours = roundedCreateDate.getHour();
        int minutes = roundedCreateDate.getMinute();
        int seconds = roundedCreateDate.getSecond();
        // Tạo chuỗi ngày và giờ
        String formattedDate = String.format("%04d_%02d_%02d_%02dh_%02dm_%02ds", year, month, day, hours, minutes, seconds);
        // Kết hợp chuỗi đơn vị vào tên tệp
        return String.format("event%d_customer%d_%s.png", eventId, customerId, formattedDate);
    }
}