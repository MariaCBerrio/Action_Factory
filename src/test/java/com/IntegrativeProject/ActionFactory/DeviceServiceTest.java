package com.IntegrativeProject.ActionFactory;

import com.IntegrativeProject.ActionFactory.Exceptions.ApiRequestException;
import com.IntegrativeProject.ActionFactory.model.Device;
import com.IntegrativeProject.ActionFactory.model.Supplier;
import com.IntegrativeProject.ActionFactory.repository.*;
import com.IntegrativeProject.ActionFactory.service.DeviceServiceImpl;
import com.IntegrativeProject.ActionFactory.util.CsvUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class DeviceServiceTest {
    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private ValidDeviceRepository validDeviceRepository;

    @Mock
    private InvalidDeviceRepository invalidDeviceRepository;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private DeviceServiceImpl deviceService;




    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void noFieldSelected() {
        // Crear un MultipartFile falso para simular un archivo vacío
        MultipartFile file = new MockMultipartFile("file", new byte[0]);

        // Lanzar la excepción y verificar que se lanza ApiRequestException
        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.deviceService.uploadDevices(file));

        // Verificar que el mensaje de la excepción es el esperado
        assertEquals("No file selected!", e.getMessage());
    }

    @Test
    public void fieldnoCvs() {
        // Crear un contenido de archivo que no sea un CSV válido
        String content = "This is not a CSV file";
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", content.getBytes());

        // Lanzar la excepción y verificar que se lanza ApiRequestException
        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.deviceService.uploadDevices(file));

        // Verificar que el mensaje de la excepción es el esperado
        assertEquals("Please upload a CSV file!", e.getMessage());
    }

//    @Test
//    public void saveDevice() throws IOException {
//        // Crear un contenido de archivo CSV válido
//        String content = "DeviceID,DeviceName\n1,DeviceA\n2,DeviceB";
//        MultipartFile file = new MockMultipartFile("file", "devices.csv", "text/csv", content.getBytes());
//
//        // Configurar el comportamiento de los mocks
//        when(supplierRepository.existsById(anyLong())).thenReturn(true);
//
//        // Suponiendo que csvToDeviceList devuelve una lista de dispositivos de prueba
//        List<Device> deviceList = new ArrayList<>();
//        Device device1 = new Device(1L, "DeviceA", new Supplier(1L), "READY_TO_USE", 70);
//        Device device2 = new Device(2L, "DeviceB", new Supplier(2L), "READY_TO_USE", 70);
//        deviceList.add(device1);
//        deviceList.add(device2);
//
//        CsvUtility csvUtilityMock = mock(CsvUtility.class);
//        when(csvUtilityMock.csvToDeviceList(any())).thenReturn(deviceList);
//
//        deviceService.save(file);
//
//        // Verificar que los métodos de guardado se llamaron correctamente
//        verify(deviceRepository, times(1)).saveAll(anyList());
//        verify(validDeviceRepository, times(1)).saveAll(anyList());
//        verify(invalidDeviceRepository, times(1)).saveAll(anyList());
//    }

    /*@Test
    public void errorProcessing() {
        String content = "DeviceID,DeviceName\n1,DeviceA\n2,DeviceB\nInvalidData";
        MultipartFile file = new MockMultipartFile("file", "devices.csv", "text/csv", content.getBytes());

        // Lanzar la excepción y verificar que se lanza ApiRequestException
        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.deviceService.save(file));

        // Verificar que el mensaje de la excepción es el esperado
        assertEquals("Error processing devices: " + e.getMessage(), e.getMessage());
    }*/



}
