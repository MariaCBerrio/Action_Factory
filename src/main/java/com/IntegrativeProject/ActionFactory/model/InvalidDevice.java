package com.IntegrativeProject.ActionFactory.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "invalid_device")
public class InvalidDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "validation_id")
    private Long validationId;

    @OneToOne(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name ="id")
    private Device device;

    public InvalidDevice() {
    }

    public InvalidDevice(Device device) {
        this.device = device;
    }

    public Long getValidationId() {
        return validationId;
    }

    public void setValidationId(Long validationId) {
        this.validationId = validationId;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
}
