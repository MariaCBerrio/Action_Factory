package com.IntegrativeProject.ActionFactory.repository;

import com.IntegrativeProject.ActionFactory.model.InvalidDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvalidDeviceRepository extends JpaRepository<InvalidDevice, Long> {
    Optional<InvalidDevice> findByDeviceId(Long deviceId);

    void deleteByDeviceId(Long deviceId);
}
