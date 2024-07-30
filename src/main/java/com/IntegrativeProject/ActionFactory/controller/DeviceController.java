package com.IntegrativeProject.ActionFactory.controller;

import com.IntegrativeProject.ActionFactory.Exceptions.ApiRequestException;
import com.IntegrativeProject.ActionFactory.model.Device;
import com.IntegrativeProject.ActionFactory.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceServiceImpl;

    @PostMapping("/csv/upload")
    public ResponseEntity<String> uploadDevice(@RequestParam("file") MultipartFile file) {
        try {
            deviceServiceImpl.uploadDevices(file);
            String message = "The device has been uploaded successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // General exception handling
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/device-list")
    public ResponseEntity<Map<String, Object>> getDevices() {
        Map<String, Object> respDevice = new LinkedHashMap<>();
        try {
            List<Device> deviceList = deviceServiceImpl.findAll();
            if (!deviceList.isEmpty()) {
                respDevice.put("status", 1);
                respDevice.put("data", deviceList);
                return new ResponseEntity<>(respDevice, HttpStatus.OK);
            } else {
                respDevice.put("status", 0);
                respDevice.put("message", "No data found");
                return new ResponseEntity<>(respDevice, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            respDevice.put("status", 0);
            respDevice.put("message", "An error occurred: " + e.getMessage());
            return new ResponseEntity<>(respDevice, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{imei}")
    public ResponseEntity<String> getDeviceByImei(@PathVariable Long imei) {
        try {
            Device device = deviceServiceImpl.getDeviceByImei(imei);
            return ResponseEntity.ok(device.toString()); // Puedes personalizar cómo convertir el dispositivo a String
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Device with IMEI " + imei + " not found.");
        }
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<?> getDevicesBySupplier(@PathVariable Long supplierId) {
        try {
            List<Device> devices = deviceServiceImpl.getDevicesBySupplier(supplierId);
            return ResponseEntity.ok(devices);
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Supplier with ID " + supplierId + " not found.");
        }
    }

    @DeleteMapping("/{imei}")
    public ResponseEntity<String> deleteDeviceByImei(@PathVariable Long imei) {
        try {
            deviceServiceImpl.deleteDeviceByImei(imei);
            return ResponseEntity.noContent().build();
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Device with IMEI " + imei + " not found.");
        }
    }
}



