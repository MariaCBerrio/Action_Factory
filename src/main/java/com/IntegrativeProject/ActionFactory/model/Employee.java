package com.IntegrativeProject.ActionFactory.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "identification_card")
    private Long identificationCard;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "last_access")
    private LocalDateTime lastAccess;

    @Column(name = "status")
    private  String status;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = true)//el nullable significa que esta columna no puede ser nula
    private Role role;

    @OneToMany(mappedBy = "employee", cascade = {CascadeType.ALL}, orphanRemoval = true
    )
    private List<Device> devices = new ArrayList();


    public Employee() {
    }

    public Employee(Long id) {
    }

    public Employee(Long identificationCard, String name, String email, String password, LocalDate hireDate, LocalDateTime lastAccess, String status, Role role) {
        this.identificationCard = identificationCard;
        this.name = name;
        this.email = email;
        this.password = password;
        this.hireDate = hireDate;
        this.lastAccess = lastAccess;
        this.status = status;
        this.role = role;
    }

    public Long getIdentificationCard() {
        return identificationCard;
    }

    public void setIdentificationCard(Long identificationCard) {
        this.identificationCard = identificationCard;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDateTime getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(LocalDateTime lastAccess) {
        this.lastAccess = lastAccess;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}

