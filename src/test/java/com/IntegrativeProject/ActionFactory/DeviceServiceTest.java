package com.IntegrativeProject.ActionFactory;

import com.IntegrativeProject.ActionFactory.Exceptions.ApiRequestException;
import com.IntegrativeProject.ActionFactory.model.Device;
import com.IntegrativeProject.ActionFactory.model.InvalidDevice;
import com.IntegrativeProject.ActionFactory.model.Supplier;
import com.IntegrativeProject.ActionFactory.model.ValidDevice;
import com.IntegrativeProject.ActionFactory.repository.*;
import com.IntegrativeProject.ActionFactory.service.DeviceServiceImpl;
import com.IntegrativeProject.ActionFactory.util.CsvUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;



@ExtendWith(MockitoExtension.class)
public class DeviceServiceTest {

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private InvalidDeviceRepository invalidDeviceRepository;

    @Mock
    private ValidDeviceRepository validDeviceRepository;


    private Device device1;
    private Device device2;
    private Device device3;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Initialize devices
        device1 = new Device();
        device1.setImei(123456789012345L);
        device1.setSupplier(new Supplier(1L));
        device1.setStatus("READY_TO_USE");
        device1.setScore(70);

        device2 = new Device();
        device2.setImei(987654321098765L);
        device2.setSupplier(new Supplier(2L));
        device2.setStatus("READY_TO_USE");
        device2.setScore(50);

        device3 = new Device();
        device3.setImei(123456789098765L);
        device3.setSupplier(new Supplier(3L));
        device3.setStatus("CANCELLED");
        device3.setScore(80);
    }
    


    @Test
    public void testUploadDevicesEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
            deviceService.uploadDevices(file);
        });

        assertEquals("No file selected!", exception.getMessage());
    }



    @Test
    public void testUploadDevicesInvalidFormat() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "test".getBytes());

        try (MockedStatic<CsvUtility> mockedCsvUtility = mockStatic(CsvUtility.class)) {
            mockedCsvUtility.when(() -> CsvUtility.hasCsvFormat(file)).thenReturn(false);

            ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
                deviceService.uploadDevices(file);
            });

            assertEquals("Please upload a CSV file!", exception.getMessage());
        }
    }

    @Test
    public void testProcessAndSaveDevices() throws Exception {
        List<Device> deviceList = Arrays.asList(device1, device2, device3);

        when(supplierRepository.existsById(1L)).thenReturn(true);
        when(supplierRepository.existsById(2L)).thenReturn(true);
        when(supplierRepository.existsById(3L)).thenReturn(true);

        Method processAndSaveDevicesMethod = DeviceServiceImpl.class.getDeclaredMethod("processAndSaveDevices", List.class);
        processAndSaveDevicesMethod.setAccessible(true);
        processAndSaveDevicesMethod.invoke(deviceService, deviceList);

        ArgumentCaptor<List<Device>> deviceListCaptor = ArgumentCaptor.forClass(List.class);
        verify(deviceRepository, times(1)).saveAll(deviceListCaptor.capture());

        List<Device> capturedDeviceList = deviceListCaptor.getValue();
        assertEquals(deviceList.size(), capturedDeviceList.size());
        assertTrue(capturedDeviceList.containsAll(deviceList));
    }

    @Test
    public void testValidateDevices() {
        List<Device> sortedDeviceList = new ArrayList<>();
        Device device1 = new Device();
        device1.setImei(123456789012345L);
        device1.setSupplier(new Supplier(1L));
        device1.setStatus("READY_TO_USE");
        device1.setScore(70);
        sortedDeviceList.add(device1);

        Device device2 = new Device();
        device2.setImei(987654321098765L);
        device2.setSupplier(new Supplier(1L));
        device2.setStatus("READY_TO_USE");
        device2.setScore(50);
        sortedDeviceList.add(device2);

        Device device3 = new Device();
        device3.setImei(123456789098765L);
        device3.setSupplier(new Supplier(1L));
        device3.setStatus("CANCELLED");
        device3.setScore(80);
        sortedDeviceList.add(device3);

        List<ValidDevice> validDeviceList = new ArrayList<>();
        List<InvalidDevice> invalidDeviceList = new ArrayList<>();

        when(supplierRepository.existsById(anyLong())).thenReturn(true);

        deviceService.validateDevices(sortedDeviceList, validDeviceList, invalidDeviceList);

        // Verificar el contenido de las listas
        assertEquals(1, validDeviceList.size(), "El tamaño de la lista de dispositivos válidos no es el esperado");
        assertEquals(2, invalidDeviceList.size(), "El tamaño de la lista de dispositivos inválidos no es el esperado");

        // Verificar que los dispositivos correctos están en las listas
        assertTrue(validDeviceList.stream().anyMatch(vd -> vd.getDevice().getImei().equals(device1.getImei())), "El dispositivo válido no se encuentra en la lista");
        assertTrue(invalidDeviceList.stream().anyMatch(id -> id.getDevice().getImei().equals(device2.getImei())), "El dispositivo inválido con score bajo no se encuentra en la lista");
        assertTrue(invalidDeviceList.stream().anyMatch(id -> id.getDevice().getImei().equals(device3.getImei())), "El dispositivo inválido con status CANCELLED no se encuentra en la lista");
    }


    @Test
    public void testGetDeviceByImei() {
        Long imei = 123456789L;
        Device device = new Device();
        device.setImei(imei);
        when(deviceRepository.findByImei(imei)).thenReturn(Optional.of(device));

        Device result = deviceService.getDeviceByImei(imei);

        assertEquals(device, result);
    }

    @Test
    public void testGetDeviceByImeiNotFound() {
        Long imei = 123456789L;
        when(deviceRepository.findByImei(imei)).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
            deviceService.getDeviceByImei(imei);
        });

        assertEquals("Device not found with IMEI: " + imei, exception.getMessage());
    }

    @Test
    public void testGetDevicesBySupplier() {
        Long supplierId = 1L;
        Supplier supplier = new Supplier();
        supplier.setId(supplierId);
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.of(supplier));
        List<Device> devices = Arrays.asList(new Device(), new Device());
        when(deviceRepository.findBySupplierId(supplierId)).thenReturn(devices);

        List<Device> result = deviceService.getDevicesBySupplier(supplierId);

        assertEquals(devices, result);
    }

    @Test
    public void testGetDevicesBySupplierNotFound() {
        Long supplierId = 1L;
        when(supplierRepository.findById(supplierId)).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
            deviceService.getDevicesBySupplier(supplierId);
        });

        assertEquals("Supplier not found with ID: " + supplierId, exception.getMessage());
    }

    @Test
    public void testDeleteDeviceByImei() {
        Long imei = 123456789L;
        Device device = new Device();
        device.setImei(imei);
        device.setId(1L);
        when(deviceRepository.findByImei(imei)).thenReturn(Optional.of(device));
        when(invalidDeviceRepository.findByDeviceId(device.getId())).thenReturn(Optional.empty());
        when(validDeviceRepository.findByDeviceId(device.getId())).thenReturn(Optional.empty());

        deviceService.deleteDeviceByImei(imei);

        verify(deviceRepository, times(1)).delete(device);
    }

    @Test
    public void testDeleteDeviceByImeiNotFound() {
        Long imei = 123456789L;
        when(deviceRepository.findByImei(imei)).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
            deviceService.deleteDeviceByImei(imei);
        });

        assertEquals("The device with IMEI: " + imei + " not found", exception.getMessage());
    }

    @Test
    public void testFindAll() {
        List<Device> devices = Arrays.asList(new Device(), new Device());
        when(deviceRepository.findAll()).thenReturn(devices);

        List<Device> result = deviceService.findAll();

        assertEquals(devices, result);
    }
}
