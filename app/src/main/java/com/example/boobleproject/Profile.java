package com.example.boobleproject;

public class Profile {
    private int id;
    private String name;
    private int age;
    private String city;
    private int photoRes;

    public Profile(int id, String name, int age, String city, int photoRes) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.city = city;
        this.photoRes = photoRes;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public String getCity() { return city; }
    public int getPhotoRes() { return photoRes; }
}
