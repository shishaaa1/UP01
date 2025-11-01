package com.example.boobleproject;

import java.util.Date;

public class Profile {
    private int id;
    private String firstName;
    private String lastName;
    private Date birthday;
    private String gender;
    private String bio;
    private int photoRes;
    private byte[] photoBytes;

    public Profile(int id, String firstName, String lastName, Date birthday,
                   String gender, String bio,int photoRes) {
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
    public String getGender() { return gender; }
    public String getBio() { return bio; }
    public int getPhotoRes() { return photoRes; }
    public byte[] getPhotoPath() { return photoBytes; }


    public String getFullName() {
        return firstName + " " + lastName;
    }
}
