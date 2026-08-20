package com.priyankaa.enterprise_order_management_system.dto;

import com.priyankaa.enterprise_order_management_system.enums.Role;

public class UserResponse {

    private Long id;
    private String address;
    private String name;
    private String email;
    private String phone;
    private Role role;

    public UserResponse() {
    }

    public UserResponse(Long id, String address, String name, String email, String phone, Role role) {
        this.id = id;
        this.address = address;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}