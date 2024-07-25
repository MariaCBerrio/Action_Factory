package com.IntegrativeProject.ActionFactory.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "supplier")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "website")
    private String website;

    @Column(name = "industry_sector")
    private String industrySector;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    @OneToMany(mappedBy = "supplier", cascade = {CascadeType.ALL}, orphanRemoval = true
    )
    private List<Device> devices = new ArrayList();


    public Supplier() {
    }

    public Supplier(Long id) {
        this.id = id;
    }

    public Supplier(Long id, String name, String address, String phoneNumber, String email, String website, String industrySector, LocalDate registrationDate) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.industrySector = industrySector;
        this.registrationDate = registrationDate;
    }

    public Supplier(String name, String address, String phoneNumber, String email, String website, String industrySector, LocalDate registrationDate) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.industrySector = industrySector;
        this.registrationDate = registrationDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIndustrySector() {
        return industrySector;
    }

    public void setIndustrySector(String industrySector) {
        this.industrySector = industrySector;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
}

