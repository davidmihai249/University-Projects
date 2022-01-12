package com.example.socialnetworkgui.utils.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class PasswordHashing {
    public static String hashPassword(String password){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            for (byte aByte : bytes) {
                stringBuilder.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return stringBuilder.toString();
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean checkPassword(String candidate, String hashedPassword){
        return Objects.equals(hashPassword(candidate), hashedPassword);
    }
}
