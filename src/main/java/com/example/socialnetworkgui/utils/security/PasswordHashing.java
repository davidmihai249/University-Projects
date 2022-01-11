package com.example.socialnetworkgui.utils.security;

public class PasswordHashing {
    public static String hashPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt(10));
    }

    public static boolean checkPassword(String candidate, String hashedPassword){
        return BCrypt.checkpw(candidate,hashedPassword);
    }
}
