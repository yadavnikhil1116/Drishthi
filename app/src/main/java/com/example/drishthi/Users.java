package com.example.drishthi;

public class Users {
    String Username, Name, Number, Email, PermanentAdd, Latitude, Longitude, UID;

    public Users(){
    }

    public Users(String username, String name, String number, String email, String permanentadd, String uid) {
        Username = username;
        Name = name;
        Number = number;
        Email = email;
        PermanentAdd = permanentadd;
        UID = uid;
    }
    public Users(String username, String name, String number, String email, String permanentadd, String latitude, String longitude, String uid) {
        Username = username;
        Name = name;
        Number = number;
        Email = email;
        PermanentAdd = permanentadd;
        Latitude = latitude;
        Longitude = longitude;
        UID = uid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPermanentAdd() {
        return PermanentAdd;
    }

    public void setPermanentAdd(String permanentAdd) {
        PermanentAdd = permanentAdd;
    }
}
