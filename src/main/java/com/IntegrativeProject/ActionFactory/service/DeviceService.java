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
//          Eliminación de de todos los registros actuales en la base de datos antes de la carga del archivo CSV
            deleteAllDevices();
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
            // Verifica si el dispositivo tiene un empleado asociado
            if (device.getEmployee() == null) {
                logger.warn("Device with IMEI: {} has no employee", device.getImei());
                continue; // Pasa al siguiente dispositivo
            }

            // Obtén el empleado del repositorio
            Optional<Employee> optionalEmployee = employeeRepository.findById(device.getEmployee().getId());
            if (!optionalEmployee.isPresent()) {
                logger.warn("Employee with identification card {} does not exist, so the device can't be validated.", device.getEmployee().getIdentificationCard());
                continue; // Pasa al siguiente dispositivo
            }

            Employee employee = optionalEmployee.get();
            if (employee.getStatus() == null) {
                logger.warn("Employee with ID {} has null status", employee.getId());
                continue; // Pasa al siguiente dispositivo
            }

            if (!employee.getStatus().equalsIgnoreCase("Active")) {
                logger.warn("Employee with identification card {} is inactive in the factory, so the device can't be validated.", employee.getIdentificationCard());
                continue; // Pasa al siguiente dispositivo
            }

            // Verifica si el dispositivo tiene un proveedor asociado
            if (device.getSupplier() == null) {
                logger.error("Device with IMEI: {} has no supplier", device.getImei());
                device.setValidationStatus("Invalid");
                invalidDeviceList.add(new InvalidDevice(device));
                continue; // Pasa al siguiente dispositivo
            }

            // Verifica si el proveedor existe
            if (!supplierRepository.existsById(device.getSupplier().getId())) {
                logger.error("Supplier with ID {} does not exist.", device.getSupplier().getId());
                device.setValidationStatus("Invalid");
                invalidDeviceList.add(new InvalidDevice(device));
                throw new ApiRequestException("Supplier with ID " + device.getSupplier().getId() + " does not exist.");
            }

            // Verifica el estado del dispositivo
            if (device.getStatus() == null) {
                logger.warn("Device with IMEI: {} has null status", device.getImei());
                device.setValidationStatus("Invalid");
                invalidDeviceList.add(new InvalidDevice(device));
                continue; // Pasa al siguiente dispositivo
            }

            if (!device.getStatus().equalsIgnoreCase("READY_TO_USE")) {
                logger.warn("The device with IMEI: {} hasn't a status equal to READY_TO_USE", device.getImei());
                device.setValidationStatus("Invalid");
                invalidDeviceList.add(new InvalidDevice(device));
                continue; // Pasa al siguiente dispositivo
            }

            // Verifica el puntaje del dispositivo
            if (device.getScore() < 60) {
                logger.warn("The device with IMEI: {} has a score less than or equal to 60", device.getImei());
                device.setValidationStatus("Invalid");
                invalidDeviceList.add(new InvalidDevice(device));
                continue; // Pasa al siguiente dispositivo
            }

            // Verifica si el IMEI es un palíndromo
            String imeiStr = Long.toString(device.getImei());
            String reverseImeiStr = new StringBuilder(imeiStr).reverse().toString();
            if (imeiStr.equals(reverseImeiStr)) {
                logger.warn("The device with IMEI: {} has an IMEI palindrome", device.getImei());
                device.setValidationStatus("Invalid");
                invalidDeviceList.add(new InvalidDevice(device));
            } else {
                logger.info("The device with IMEI: {} has been successfully validated", device.getImei());
                device.setValidationStatus("Valid");
                validDeviceList.add(new ValidDevice(device));
            }
        }
    }



    public void deviceStatusCheck(List<Device> sortedDeviceList) {
        for (Device device : sortedDeviceList) {
            logger.info("Device with IMEI {} will enter verification process.", device.getImei());
            if (device.getStatus() == null) {
                logger.warn("Device with IMEI {} has null status.", device.getImei());
                continue;
            }
            switch (device.getStatus()) {
                case "READY_TO_USE":
                    break;
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
        validDeviceRepository.deleteAll();
        invalidDeviceRepository.deleteAll();;
        deviceRepository.deleteAll();
    }
}
