package com.priyankaa.enterprise_order_management_system.entity;

import com.priyankaa.enterprise_order_management_system.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name="Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100)
    private String name;

    @Column(nullable=false, unique=true, length=150)
    private String email;

    @Column(length=15)
    private String phone;

    @Column(length=300)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private Role role;
}
