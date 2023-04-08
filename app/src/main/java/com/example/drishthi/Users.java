package com.example.drishthi;

public class Users {
    String Name, Number, Email, Latitude, Longitude, UID;

    public Users(){
    }

    public Users(String name, String number, String email, String uid) {
        Name = name;
        Number = number;
        Email = email;
        UID = uid;
    }
    public Users(String name, String number, String email, String latitude, String longitude, String uid) {
        Name = name;
        Number = number;
        Email = email;
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
}
