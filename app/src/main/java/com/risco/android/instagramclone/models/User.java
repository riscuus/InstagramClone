package com.risco.android.instagramclone.models;

/**
 * Created by Albert Risco on 02/12/2017.
 */

public class User  {
    private String user_id;
    private String username;
    private String email;
    private long phone_number;

    public User(String user_id, String username, String email, long phone_number) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.phone_number = phone_number;
    }

    public User() {
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone_number='" + phone_number + '\'' +
                '}';
    }
}
