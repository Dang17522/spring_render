package com.zalo.Spring_Zalo.Password_User_Web;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.ResourceBundle;

public class PasswordUtils {

    // Mã hóa mật khẩu
    public static String encryptPassword(String password) {
        try {
            ResourceBundle secretkeyPassword = ResourceBundle.getBundle("application");
            String secretKey = secretkeyPassword.getString("secretkeyPassword");
            System.out.println(">>SECRET :"+secretKey);
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey key = new SecretKeySpec(secretKey.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(password.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Giải mã mật khẩu
    public static String decryptPassword(String encryptedPassword) {
        try {
            ResourceBundle secretkeyPassword = ResourceBundle.getBundle("application");
            String secretKey = secretkeyPassword.getString("secretkeyPassword");
            System.out.println(">>SECRET :"+secretKey);
            Cipher cipher = Cipher.getInstance("AES");
            SecretKey key = new SecretKeySpec(secretKey.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
            return new String(decrypted,StandardCharsets.UTF_8);
        }  catch (Exception e) {
            e.printStackTrace();
            // Xử lý lỗi, ví dụ:
            System.err.println("Error during password decryption: " + e.getMessage());
            return "Decryption failed";
        }
    }
    /**
     * this methob use to test the code runs correctly
     * @param args
     */
    public static void main(String[] args) {
        String originalPassword = "staff";
        // String secretKey = "Zalo_Admin_Web_2024"; // Thay thế bằng một khóa bí mật thực tế

        // Mã hóa mật khẩu
        String encryptedPassword = encryptPassword(originalPassword);
        System.out.println("Encrypted Password: " + encryptedPassword);

        // Giải mã mật khẩu
        String decryptedPassword = decryptPassword(encryptedPassword);
        System.out.println("Decrypted Password: " + decryptedPassword);
    } 

}
