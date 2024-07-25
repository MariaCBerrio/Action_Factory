package com.IntegrativeProject.ActionFactory.service;

import com.IntegrativeProject.ActionFactory.Exceptions.ApiRequestException;
import com.IntegrativeProject.ActionFactory.model.Device;
import com.IntegrativeProject.ActionFactory.model.InvalidDevice;
import com.IntegrativeProject.ActionFactory.model.Supplier;
import com.IntegrativeProject.ActionFactory.model.ValidDevice;
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
public class DeviceServiceImpl implements DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

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

    private List<Device> readDevicesFromCsv(MultipartFile file) throws IOException {
        CsvUtility csvUtility = new CsvUtility(employeeRepository, supplierRepository);
        return csvUtility.csvToDeviceList(file.getInputStream());
    }

    private void processAndSaveDevices(List<Device> deviceList) {
        List<InvalidDevice> invalidDeviceList = new ArrayList<>();
        List<ValidDevice> validDeviceList = new ArrayList<>();

        List<Device> sortedDeviceList = deviceList.stream()
                .sorted((d1, d2) -> d1.getImei().compareTo(d2.getImei()))
                .collect(Collectors.toList());

        deviceRepository.saveAll(sortedDeviceList);
        validateDevices(sortedDeviceList, validDeviceList, invalidDeviceList);

        validDeviceRepository.saveAll(validDeviceList);
        invalidDeviceRepository.saveAll(invalidDeviceList);
    }

    private void validateDevices(List<Device> sortedDeviceList, List<ValidDevice> validDeviceList, List<InvalidDevice> invalidDeviceList) {
        for (Device device : sortedDeviceList) {
            if (supplierRepository.existsById(device.getSupplier().getId())) {
                if (device.getStatus().equalsIgnoreCase("READY_TO_USE")) {
                    if (device.getScore() > 60) {
                        String imeiStr = Long.toString(device.getImei());
                        String reverseImeiStr = new StringBuilder(imeiStr).reverse().toString();
                        if (!imeiStr.equals(reverseImeiStr)) {
                            logger.info("The device with imei: {} has been successfully validated", device.getImei());
                            validDeviceList.add(new ValidDevice(device));
                        } else {
                            logger.warn("The device with imei: {} has an imei palindrome", device.getImei());
                            invalidDeviceList.add(new InvalidDevice(device));
                        }
                    } else {
                        logger.warn("The device with imei: {} has a score less than or equal to 60", device.getImei());
                        invalidDeviceList.add(new InvalidDevice(device));
                    }
                } else {
                    logger.warn("The device with imei: {} has a status equal to CANCELLED", device.getImei());
                    invalidDeviceList.add(new InvalidDevice(device));
                }
            } else {
                logger.error("Supplier with ID {} does not exist.", device.getSupplier().getId());
                invalidDeviceList.add(new InvalidDevice(device));
                throw new ApiRequestException("Supplier with ID " + device.getSupplier().getId() + " does not exist.");
            }
        }
    }

    @Override
    public List<Device> findAll() {
        return deviceRepository.findAll();
    }

    public Device getDeviceByImei(Long imei) {
        Optional<Device> optionalDevice = deviceRepository.findByImei(imei);
        return optionalDevice.orElseThrow(() -> new ApiRequestException("Device not found with IMEI: " + imei));
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
            Optional<Device> deviceOptional = deviceRepository.findByImei(imei);
            if (deviceOptional.isPresent()) {
                Device device = deviceOptional.get();

                Optional<InvalidDevice> invalidDeviceOptional = invalidDeviceRepository.findByDeviceId(device.getId());
                if (invalidDeviceOptional.isPresent()) {
                    invalidDeviceRepository.deleteByDeviceId(device.getId());
                } else {
                    Optional<ValidDevice> validDeviceOptional = validDeviceRepository.findByDeviceId(device.getId());
                    if (validDeviceOptional.isPresent()) {
                        validDeviceRepository.deleteByDeviceId(device.getId());
                    }
                }

                deviceRepository.delete(device);
            } else {
                throw new ApiRequestException("The device with IMEI: " + imei + " not found");
            }
        } catch (DataIntegrityViolationException e) {
            throw new ApiRequestException("Cannot delete device with IMEI: " + imei + " due to foreign key constraints", e);
        }
    }

    public void deleteAllDevices() {
        deviceRepository.deleteAll();
    }
}
