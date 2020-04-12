package com.app.edugain.models;

public class User
{
    private String address;
    private String email;
    private String id;
    private String name;
    private String phone;
    private String role;

    public User(String address, String email, String id, String name, String phone, String role) {
        this.address = address;
        this.email = email;
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.role = role;
    }

    public User() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
