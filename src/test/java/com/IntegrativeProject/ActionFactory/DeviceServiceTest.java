package com.IntegrativeProject.ActionFactory;

import com.IntegrativeProject.ActionFactory.Exceptions.ApiRequestException;
import com.IntegrativeProject.ActionFactory.model.*;
import com.IntegrativeProject.ActionFactory.repository.*;
import com.IntegrativeProject.ActionFactory.service.DeviceService;
import com.IntegrativeProject.ActionFactory.util.CsvUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeviceServiceTest {

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

    @InjectMocks
    private DeviceService deviceService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUploadDevicesEmptyFile() {
        // Crear un archivo vacío
        MockMultipartFile file = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);

        // Verificar que se lanza la excepción esperada
        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
            deviceService.uploadDevices(file);
        });

        // Verificar que el mensaje de la excepción es el esperado
        assertEquals("No file selected!", exception.getMessage());
    }

    @Test
    public void testUploadDevicesInvalidFormat() {
        // Crear un archivo con contenido no CSV
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "test".getBytes());

        // Simular el comportamiento de CsvUtility.hasCsvFormat
        try (MockedStatic<CsvUtility> mockedCsvUtility = mockStatic(CsvUtility.class)) {
            mockedCsvUtility.when(() -> CsvUtility.hasCsvFormat(file)).thenReturn(false);

            // Verificar que se lanza la excepción esperada
            ApiRequestException exception = assertThrows(ApiRequestException.class, () -> {
                deviceService.uploadDevices(file);
            });

            // Verificar que el mensaje de la excepción es el esperado
            assertEquals("Please upload a CSV file!", exception.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testProcessAndSaveDevices() throws Exception {
        // Paso 1: Configura los objetos de prueba
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setIdentificationCard(123456L);
        Device device1 = new Device();
        device1.setImei(123456789012345L);
        device1.setEmployee(employee1);
        device1.setStatus("NEW");

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setIdentificationCard(654321L);
        Device device2 = new Device();
        device2.setImei(123456789098765L);
        device2.setEmployee(employee2);
        device2.setStatus("USED");

        Employee employee3 = new Employee();
        employee3.setId(3L);
        employee3.setIdentificationCard(987654L);
        Device device3 = new Device();
        device3.setImei(987654321098765L);
        device3.setEmployee(employee3);
        device3.setStatus("NEW");

        List<Device> deviceList = Arrays.asList(device1, device2, device3);

        // Paso 2: Configura los mocks
        when(supplierRepository.existsById(anyLong())).thenReturn(true);
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(new Employee()));

        // Paso 3: Invoca el método a probar utilizando reflexión
        Method processAndSaveDevicesMethod = DeviceService.class.getDeclaredMethod("processAndSaveDevices", List.class);
        processAndSaveDevicesMethod.setAccessible(true);
        processAndSaveDevicesMethod.invoke(deviceService, deviceList);

        // Paso 4: Captura y verifica los argumentos
        ArgumentCaptor<List<Device>> deviceListCaptor = ArgumentCaptor.forClass((Class<List<Device>>) (Class<?>) List.class);
        verify(deviceRepository, times(1)).saveAll(deviceListCaptor.capture());

        // Verifica que la lista capturada es la esperada
        List<Device> capturedDeviceList = deviceListCaptor.getValue();
        assertEquals(deviceList.size(), capturedDeviceList.size());
        assertTrue(capturedDeviceList.containsAll(deviceList));
    }

    @Test
    public void testValidateDevices() {
        // Step 1: Set up test objects
        Employee activeEmployee = new Employee();
        activeEmployee.setId(1L);
        activeEmployee.setStatus("Active");
        activeEmployee.setIdentificationCard(123456L);

        Employee inactiveEmployee = new Employee();
        inactiveEmployee.setId(2L);
        inactiveEmployee.setStatus("Inactive");
        inactiveEmployee.setIdentificationCard(654321L);

        Supplier validSupplier = new Supplier();
        validSupplier.setId(1L);
        validSupplier.setName("Valid Supplier");

        // Valid device
        Device validDevice = new Device();
        validDevice.setImei(123456789012345L);
        validDevice.setEmployee(activeEmployee);
        validDevice.setStatus("READY_TO_USE");
        validDevice.setScore(70);
        validDevice.setSupplier(validSupplier);

        // Invalid device due to score
        Device invalidDeviceDueToScore = new Device();
        invalidDeviceDueToScore.setImei(123456789098765L);
        invalidDeviceDueToScore.setEmployee(activeEmployee);
        invalidDeviceDueToScore.setStatus("READY_TO_USE");
        invalidDeviceDueToScore.setScore(50);
        invalidDeviceDueToScore.setSupplier(validSupplier);

        // Invalid device due to palindrome IMEI
        Device invalidDeviceDueToPalindrome = new Device();
        invalidDeviceDueToPalindrome.setImei(12345654321L);
        invalidDeviceDueToPalindrome.setEmployee(activeEmployee);
        invalidDeviceDueToPalindrome.setStatus("READY_TO_USE");
        invalidDeviceDueToPalindrome.setScore(80);
        invalidDeviceDueToPalindrome.setSupplier(validSupplier);

        // List of devices
        List<Device> sortedDeviceList = Arrays.asList(validDevice, invalidDeviceDueToScore, invalidDeviceDueToPalindrome);
        List<ValidDevice> validDeviceList = new ArrayList<>();
        List<InvalidDevice> invalidDeviceList = new ArrayList<>();

        // Step 2: Set up mocks
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(activeEmployee));
        when(employeeRepository.findById(2L)).thenReturn(Optional.of(inactiveEmployee));
        when(supplierRepository.existsById(1L)).thenReturn(true); // Valid supplier
        when(supplierRepository.existsById(2L)).thenReturn(false); // Invalid supplier for tests

        // Step 3: Invoke the method under test
        deviceService.validateDevices(sortedDeviceList, validDeviceList, invalidDeviceList);

        // Step 4: Verify results
        assertEquals(1, validDeviceList.size(), "The size of the valid devices list is not as expected");
        assertEquals(2, invalidDeviceList.size(), "The size of the invalid devices list is not as expected");

        // Verify that the valid device is in the valid devices list
        assertTrue(validDeviceList.stream()
                .anyMatch(vd -> vd.getDevice().getImei() == 123456789012345L), "The valid device is not in the list of valid devices");

        // Verify that the invalid devices are in the invalid devices list
        assertTrue(invalidDeviceList.stream()
                .anyMatch(id -> id.getDevice().getImei() == 123456789098765L), "The device invalid due to score is not in the list of invalid devices");
        assertTrue(invalidDeviceList.stream()
                .anyMatch(id -> id.getDevice().getImei() == 12345654321L), "The device invalid due to palindrome IMEI is not in the list of invalid devices");
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
