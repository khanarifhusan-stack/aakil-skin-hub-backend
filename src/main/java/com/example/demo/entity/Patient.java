package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String mobile;

    private Integer age;

    private String gender;

    private String skinCondition;

    public Patient() {
    }

    public Patient(
            String name,
            String mobile,
            Integer age,
            String gender,
            String skinCondition) {

        this.name = name;
        this.mobile = mobile;
        this.age = age;
        this.gender = gender;
        this.skinCondition = skinCondition;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSkinCondition() {
        return skinCondition;
    }

    public void setSkinCondition(String skinCondition) {
        this.skinCondition = skinCondition;
    }
}