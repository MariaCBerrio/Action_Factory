package com.IntegrativeProject.ActionFactory.repository;

import com.IntegrativeProject.ActionFactory.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByImei(Long imei);

    List<Device> findBySupplierId(Long supplierId);

    List<Device> findByValidationStatus(String validationStatus);
}
