package com.example.fitzone;

public class UserProfile {

    private final long id;
    private final String name;
    private final String email;
    private final int age;
    private final float height;
    private final float weight;

    public UserProfile(long id, String name, String email, int age, float height, float weight) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.age = age;
        this.height = height;
        this.weight = weight;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public float getHeight() {
        return height;
    }

    public float getWeight() {
        return weight;
    }
}

