package com.IntegrativeProject.ActionFactory;

import com.IntegrativeProject.ActionFactory.Exceptions.ApiRequestException;
import com.IntegrativeProject.ActionFactory.model.Supplier;
import com.IntegrativeProject.ActionFactory.repository.SupplierRepository;
import com.IntegrativeProject.ActionFactory.service.SupplierService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SupplierServiceTest {

    //Add Dependencies
    private SupplierService supplierService;
    private SupplierRepository supplierRepository;
    //Junit 5!

    @BeforeEach
    public void setUp(){
        //Mock
        this.supplierRepository = mock(SupplierRepository.class);
        //PREGUNTA: Se debe inyectar también el repositorio?
        this.supplierService = new SupplierService(supplierRepository);
    }
    //Generar los casos de prueba

    @Test
    public void createSupplierWithNullName(){
        //Arrange
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setAddress("House 1");
        supplier.setPhoneNumber("3124567890");
        supplier.setEmail("test@mymail.com");
        supplier.setWebsite("www.test.com");
        supplier.setIndustrySector("Sector 1");
        supplier.setRegistrationDate(LocalDate.of(2023,05,30));

        //Act & Assert
        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.supplierService.createSupplier(supplier));
        assertEquals("Name not valid, check field",e.getMessage());
    }
    @Test
    public void createSupplierWithNullAddress(){
        //Arrange
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Supplier");
        supplier.setPhoneNumber("3124567890");
        supplier.setEmail("test@mymail.com");
        supplier.setWebsite("www.test.com");
        supplier.setIndustrySector("Sector 1");
        supplier.setRegistrationDate(LocalDate.of(2023,05,30));

        //Act & Assert
        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.supplierService.createSupplier(supplier));
        assertEquals("Address not valid, check field",e.getMessage());
    }
    @Test
    public void createSupplierWithNullPhoneNumber(){
        //Arrange
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Supplier");
        supplier.setAddress("House 1");
        supplier.setEmail("test@mymail.com");
        supplier.setWebsite("www.test.com");
        supplier.setIndustrySector("Sector 1");
        supplier.setRegistrationDate(LocalDate.of(2023,05,30));

        //Act & Assert
        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.supplierService.createSupplier(supplier));
        assertEquals("Phone number not valid, check field",e.getMessage());
    }

    @Test
    public void createSupplierWithNullEmail(){
        //Arrange
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Supplier");
        supplier.setAddress("House 1");
        supplier.setPhoneNumber("3124567890");
        supplier.setWebsite("www.test.com");
        supplier.setIndustrySector("Sector 1");
        supplier.setRegistrationDate(LocalDate.of(2023,05,30));

        //Act & Assert
        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.supplierService.createSupplier(supplier));
        assertEquals("Email not valid, check field",e.getMessage());
    }
    @Test
    public void createSupplierWithInvalidEmail(){
        Supplier supplier = new Supplier(1L,"Supplier 1","House 1","3113214456","testmymail.com","www.test.com","Sector 1",LocalDate.of(2020,8,24));

        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.supplierService.createSupplier(supplier));
        assertEquals("Email not valid, check field",e.getMessage());
    }

    @Test
    public void createSupplierWithNullWebsite(){
        //Arrange
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Supplier");
        supplier.setAddress("House 1");
        supplier.setPhoneNumber("3124567890");
        supplier.setEmail("test@mymail.com");
        supplier.setIndustrySector("Sector 1");
        supplier.setRegistrationDate(LocalDate.of(2023,05,30));

        //Act & Assert
        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.supplierService.createSupplier(supplier));
        assertEquals("Website not valid, check field",e.getMessage());
    }

    @Test
    public void createSupplierWithInvalidWebsite(){
        Supplier supplier = new Supplier(1L,"Supplier 1","House 1","3113214456","test@mymail.com","ww.test.com","Sector 1",LocalDate.of(2020,8,24));

        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.supplierService.createSupplier(supplier));
        assertEquals("Website not valid, check field",e.getMessage());
    }

    @Test
    public void createSupplierWithNullIndustrySector(){
        //Arrange
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Supplier");
        supplier.setAddress("House 1");
        supplier.setPhoneNumber("3124567890");
        supplier.setEmail("test@mymail.com");
        supplier.setWebsite("www.test.com");
        supplier.setRegistrationDate(LocalDate.of(2023,05,30));

        //Act & Assert
        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.supplierService.createSupplier(supplier));
        assertEquals("Industry sector not valid, check field",e.getMessage());
    }

    @Test
    public void createSupplierWithNullRegistrationDate(){
        //Arrange
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Supplier");
        supplier.setAddress("House 1");
        supplier.setPhoneNumber("3124567890");
        supplier.setEmail("test@mymail.com");
        supplier.setWebsite("www.test.com");
        supplier.setIndustrySector("Sector 1");

        //Act & Assert
        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.supplierService.createSupplier(supplier));
        assertEquals("Registration date not valid, check field",e.getMessage());
    }
    @Test
    public void createSupplierWithInvalidRegistrationDate(){
        Supplier supplier = new Supplier(1L,"Supplier 1","House 1","3113214456","test@mymail.com","www.test.com","Sector 1",LocalDate.of(2025,8,24));

        ApiRequestException e = assertThrows(ApiRequestException.class, () ->
                this.supplierService.createSupplier(supplier));
        assertEquals("Registration date not valid, check field",e.getMessage());
    }

    @Test
    public void createSupplierWhenAlreadyExists(){
        Supplier supplier = new Supplier(1L,"Supplier 1","House 1","3113214456","test@mymail.com","www.test.com","Sector 1",LocalDate.of(2020,8,24));
        Supplier supplier2 = new Supplier(2L, "Supplier 2", "House 2", "3223225567", "test@mymail.com", "http://www.example.com", "Sector 2", LocalDate.of(2021, 9, 25));

        List<Supplier> suppliers = Collections.singletonList(supplier);
        when(supplierRepository.findAll()).thenReturn(suppliers);

        // Act & Assert
        ApiRequestException e = assertThrows(ApiRequestException.class, () -> {
            supplierService.createSupplier(supplier2);
        });
        assertEquals("Supplier Already exists",e.getMessage());
    }

    @Test
    public void getNotExistingSuppliers(){
        Long invalidId= 2L;
        Supplier supplier = new Supplier(1L,"Supplier 1","House 1","3113214456","test@mymail.com","www.test.com","Sector 1",LocalDate.of(2020,8,24));
        supplierRepository.save(supplier);
        ApiRequestException e = assertThrows(ApiRequestException.class, () -> {
            supplierService.getSupplierById(invalidId);});
        assertEquals("Supplier not found, try again with a valid id",e.getMessage());
    }

    @Test
    public void getEmptySuppliersList(){
        ApiRequestException e = assertThrows(ApiRequestException.class, () -> {
            supplierService.getAllSuppliers();});
        assertEquals("There is no Suppliers to show, try saving one",e.getMessage());
    }

    @Test
    public void updateNotExistingSupplier(){
        Supplier supplier = new Supplier();
        supplier.setId(2L);

        ApiRequestException e = assertThrows(ApiRequestException.class, () -> {
            supplierService.updateSupplier(supplier);});
        assertEquals("Supplier not found, try again with a valid id",e.getMessage());
    }
    @Test

    public void deleteNotExistingSupplier(){
        Long invalidId = 1L;
        ApiRequestException e = assertThrows(ApiRequestException.class, () -> {
            supplierService.deleteSupplier(invalidId);});
        assertEquals("Supplier not found, try again with a valid id",e.getMessage());
    }

    @Test
    public void createSuppliersWhenOneAlreadyExists() {
        // Arrange
        Supplier supplier1 = new Supplier(1L, "Supplier 1", "House 1", "3113214456", "test1@mymail.com", "www.test1.com", "Sector 1", LocalDate.of(2020, 8, 24));
        Supplier supplier2 = new Supplier(2L, "Supplier 2", "House 2", "3223225567", "test1@mymail.com", "www.test2.com", "Sector 2", LocalDate.of(2021, 9, 25));

        List<Supplier> suppliers = Arrays.asList(supplier1, supplier2);

        when(supplierRepository.findAll()).thenReturn(List.of(supplier1));

        // Act & Assert
        ApiRequestException e = assertThrows(ApiRequestException.class, () -> {
            supplierService.createSuppliers(suppliers);
        });
        assertEquals("Supplier Already exists", e.getMessage());

        // Verify that saveAll was never called
        Mockito.verify(supplierRepository, Mockito.never()).saveAll(Mockito.anyList());
    }
}

