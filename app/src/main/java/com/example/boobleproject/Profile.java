package com.example.boobleproject;

import java.util.Date;

public class Profile {
    public int id;
    public String firstName;
    public String lastName;
    public Date birthday;
    public boolean gender;
    public String bio;
    public int photoRes;
    public byte[] photoBytes;

    public Profile(int id, String firstName, String lastName, Date birthday,
                  boolean gender, String bio,int photoRes) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.gender = gender;
        this.bio = bio;
        this.photoRes = photoRes;
    }



    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Date getBirthday() { return birthday; }
    public String getGenderAsString() {
        return gender ? "Мужской" : "Женский";
    }
    public void setGender(boolean gender) {
        this.gender = gender;
    }
    public String getBio() { return bio; }
    public int getPhotoRes() { return photoRes; }
    public byte[] getPhotoPath() { return photoBytes; }


    public String getFullName() {
        return firstName + " " + lastName;
    }
}
