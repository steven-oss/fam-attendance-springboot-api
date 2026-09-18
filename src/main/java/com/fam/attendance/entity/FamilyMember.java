package com.fam.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "family_member")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FamilyMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String gender;

    @Column(length = 30)
    private String phone;

    @Column(length = 255)
    private String address;

    public FamilyMember(String name, String gender, String phone, String address) {
        this.name = name;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
    }
}
