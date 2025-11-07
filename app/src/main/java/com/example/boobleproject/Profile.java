package com.example.boobleproject;

import java.util.Calendar;
import java.util.Date;

public class Profile {
    public int id;
    public String firstName;
    public String lastName;
    public Date birthday;
    public boolean sex;
    public String bio;
    public int photoRes;
    public String photoBytes;

    public Profile(int id, String firstName, String lastName, Date birthday,
                  boolean sex, String bio,int photoRes) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.sex = sex;
        this.bio = bio;
        this.photoRes = photoRes;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public Date getBirthday() { return birthday; }
    public String getGenderAsString() {
        return sex ? "Мужской" : "Женский"; // true = Мужской, false = Женский
    }
    public void setGender(boolean gender) {
        this.sex = gender;
    }
    public String getBio() { return bio; }
    public int getPhotoRes() { return photoRes; }
    public String getPhotoBytes() { return photoBytes; }

    public int getAge() {
        if (birthday == null) return 0;

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(birthday);

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
