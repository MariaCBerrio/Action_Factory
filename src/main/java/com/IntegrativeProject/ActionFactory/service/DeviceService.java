package com.IntegrativeProject.ActionFactory.service;

import com.IntegrativeProject.ActionFactory.model.Device;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface DeviceService {
    List<Device> findAll();
}
