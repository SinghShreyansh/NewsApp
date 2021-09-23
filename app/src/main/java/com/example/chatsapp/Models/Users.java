package com.example.chatsapp.Models;

public class Users {

    private String uid,name,phoneNumber,profilePic,token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Users(){
    }

    public Users(String uid, String name, String phoneNumber, String profilePic) {
        this.uid = uid;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profilePic = profilePic;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
