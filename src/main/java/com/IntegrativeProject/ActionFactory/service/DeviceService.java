package com.IntegrativeProject.ActionFactory.service;

import com.IntegrativeProject.ActionFactory.Exceptions.ApiRequestException;
import com.IntegrativeProject.ActionFactory.model.*;
import com.IntegrativeProject.ActionFactory.repository.*;
import com.IntegrativeProject.ActionFactory.util.CsvUtility;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private InvalidDeviceRepository invalidDeviceRepository;

    @Autowired
    private ValidDeviceRepository validDeviceRepository;

    public void uploadDevices(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiRequestException("No file selected!");
        }

        if (!CsvUtility.hasCsvFormat(file)) {
            throw new ApiRequestException("Please upload a CSV file!");
        }

        try {
            List<Device> deviceList = readDevicesFromCsv(file);
            processAndSaveDevices(deviceList);
        } catch (IOException e) {
            throw new ApiRequestException("Failed to parse CSV file: " + e.getMessage());
        }
    }

    public List<Device> readDevicesFromCsv(MultipartFile file) throws IOException {
        CsvUtility csvUtility = new CsvUtility(employeeRepository, supplierRepository);
        return csvUtility.csvToDeviceList(file.getInputStream());
    }

    public void processAndSaveDevices(List<Device> deviceList) {
        List<InvalidDevice> invalidDeviceList = new ArrayList<>();
        List<ValidDevice> validDeviceList = new ArrayList<>();

        List<Device> sortedDeviceList = deviceList.stream()
                .sorted((d1, d2) -> d1.getImei().compareTo(d2.getImei()))
                .collect(Collectors.toList());

        deviceStatusCheck(sortedDeviceList);
        validateDevices(sortedDeviceList, validDeviceList, invalidDeviceList);

        deviceRepository.saveAll(sortedDeviceList);
        validDeviceRepository.saveAll(validDeviceList);
        invalidDeviceRepository.saveAll(invalidDeviceList);
    }

    public void validateDevices(List<Device> sortedDeviceList, List<ValidDevice> validDeviceList, List<InvalidDevice> invalidDeviceList) {
        for (Device device : sortedDeviceList) {
            Optional<Employee> optionalEmployee = employeeRepository.findById(device.getEmployee().getId());

            if (optionalEmployee.isPresent()) {
                Employee employee = optionalEmployee.get();
                if (employee.getStatus().equalsIgnoreCase("Active")) {
                    if (supplierRepository.existsById(device.getSupplier().getId())) {
                        if (device.getStatus().equalsIgnoreCase("READY_TO_USE")) {
                            if (device.getScore() > 60) {
                                String imeiStr = Long.toString(device.getImei());
                                String reverseImeiStr = new StringBuilder(imeiStr).reverse().toString();
                                if (!imeiStr.equals(reverseImeiStr)) {
                                    logger.info("The device with IMEI: {} has been successfully validated", device.getImei());
                                    device.setValidationStatus("Valid");
                                    validDeviceList.add(new ValidDevice(device));
                                } else {
                                    logger.warn("The device with IMEI: {} has an IMEI palindrome", device.getImei());
                                    device.setValidationStatus("Invalid");
                                    invalidDeviceList.add(new InvalidDevice(device));
                                }
                            } else {
                                logger.warn("The device with IMEI: {} has a score less than or equal to 60", device.getImei());
                                device.setValidationStatus("Invalid");
                                invalidDeviceList.add(new InvalidDevice(device));
                            }
                        } else {
                            logger.warn("The device with IMEI: {} hasn't a status equal to READY_TO_USE", device.getImei());
                            device.setValidationStatus("Invalid");
                            invalidDeviceList.add(new InvalidDevice(device));
                        }
                    } else {
                        logger.error("Supplier with ID {} does not exist.", device.getSupplier().getId());
                        device.setValidationStatus("Invalid");
                        invalidDeviceList.add(new InvalidDevice(device));
                        throw new ApiRequestException("Supplier with ID " + device.getSupplier().getId() + " does not exist.");
                    }
                } else {
                    logger.warn("Employee with identification card {} is inactive in the factory, so the device can't be validated.", device.getEmployee().getIdentificationCard());
                }
            } else {
                logger.warn("Employee with identification card {} does not exist, so the device can't be validated.", device.getEmployee().getIdentificationCard());
            }
        }
    }

    public void deviceStatusCheck(List<Device> sortedDeviceList) {
        for (Device device : sortedDeviceList) {
            logger.info("Device with IMEI {} will enter verification process.", device.getImei());
            switch (device.getStatus()) {
                case "NEW":
                case "USED":
                    device.setStatus("READY_TO_USE");
                    break;
                case "OBSOLETE":
                case "DAMAGED":
                    logger.warn("Device with IMEI {} has status '{}'. Therefore, it can't be available.", device.getImei(), device.getStatus());
                    break;
                case "DEFECTIVE":
                    logger.warn("Device with IMEI {} has status '{}'. It will enter a state of PENDING_TECHNICAL_REVIEW.", device.getImei(), device.getStatus());
                    break;
                default:
                    logger.error("Device with IMEI {} has an unknown status: '{}'.", device.getImei(), device.getStatus());
                    break;
            }
        }
    }

    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    public Device getDeviceByImei(Long imei) {
        return deviceRepository.findByImei(imei)
                .orElseThrow(() -> new ApiRequestException("Device not found with IMEI: " + imei));
    }

    public List<Device> getDevicesBySupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new ApiRequestException("Supplier not found with ID: " + supplierId));

        List<Device> devices = deviceRepository.findBySupplierId(supplierId);
        if (devices.isEmpty()) {
            throw new ApiRequestException("The supplier " + supplier.getName() + " doesn't have associated devices.");
        }
        return devices;
    }

    public void deleteDeviceByImei(Long imei) {
        try {
            Device device = deviceRepository.findByImei(imei)
                    .orElseThrow(() -> new ApiRequestException("The device with IMEI: " + imei + " not found"));

            invalidDeviceRepository.findByDeviceId(device.getId())
                    .ifPresent(invalidDevice -> invalidDeviceRepository.deleteByDeviceId(device.getId()));

            validDeviceRepository.findByDeviceId(device.getId())
                    .ifPresent(validDevice -> validDeviceRepository.deleteByDeviceId(device.getId()));

            deviceRepository.delete(device);
        } catch (DataIntegrityViolationException e) {
            throw new ApiRequestException("Cannot delete device with IMEI: " + imei + " due to foreign key constraints", e);
        }
    }

    public void deleteAllDevices() {
        deviceRepository.deleteAll();
    }
}
