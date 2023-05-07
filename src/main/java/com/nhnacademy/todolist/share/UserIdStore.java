package com.nhnacademy.todolist.share;

public class UserIdStore {
    private UserIdStore(){
        throw new IllegalStateException("Utility class");
    }

    private static final ThreadLocal<String> userIdLocal = new ThreadLocal<>();

    public static void setUserId(String userId){
        userIdLocal.set(userId);
    }

    public static String getUserId(){
        return userIdLocal.get();
    }

    public static void remove(){
        userIdLocal.remove();
    }

}
