package com.IntegrativeProject.ActionFactory.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imei", unique = true)
    private Long imei;

    @Column(name = "status")
    private String status;

    @Column(name = "score")
    private int score;

    @Column(name = "validation_status")
    private String validationStatus;

    @Column(name = "validation_date")
    private LocalDateTime validationDate;

    @ManyToOne
    @JoinColumn(name = "supplier_id", referencedColumnName = "id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @OneToOne(
            mappedBy = "device",
            cascade = {CascadeType.ALL},
            orphanRemoval = true
    )
    private InvalidDevice invalidDevice;
    @OneToOne(
            mappedBy = "device",
            cascade = {CascadeType.ALL},
            orphanRemoval = true
    )
    private ValidDevice validDevice;

    public Device(){
    }

    public Device(Long imei, String status, int score, String validationStatus, LocalDateTime validationDate, Supplier supplier, Employee employee) {
        this.imei = imei;
        this.status = status;
        this.score = score;
        this.validationStatus = validationStatus;
        this.validationDate = validationDate;
        this.supplier = supplier;
        this.employee = employee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getImei() {
        return imei;
    }

    public void setImei(Long imei) {
        this.imei = imei;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(String validationStatus) {
        this.validationStatus = validationStatus;
    }

    public LocalDateTime getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(LocalDateTime validationDate) {
        this.validationDate = validationDate;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
}
