package com.IntegrativeProject.ActionFactory.repository;

import com.IntegrativeProject.ActionFactory.model.ValidDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ValidDeviceRepository extends JpaRepository<ValidDevice, Long> {
    Optional<ValidDevice> findByDeviceId(Long deviceId);
    void deleteByDeviceId(Long deviceId);
}
