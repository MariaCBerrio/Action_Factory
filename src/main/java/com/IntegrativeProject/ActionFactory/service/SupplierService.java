package com.IntegrativeProject.ActionFactory.service;

import com.IntegrativeProject.ActionFactory.model.Employee;
import com.IntegrativeProject.ActionFactory.model.Role;
import com.IntegrativeProject.ActionFactory.model.Supplier;
import com.IntegrativeProject.ActionFactory.repository.EmployeeRepository;
import com.IntegrativeProject.ActionFactory.repository.SupplierRepository;
import com.IntegrativeProject.ActionFactory.Exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SupplierService {
    private final SupplierRepository supplierRepository;

    @Autowired
    public SupplierService(SupplierRepository supplierRepository, EmployeeRepository employeeRepository){
        this.supplierRepository = supplierRepository;
    }

    @Autowired
    private EmployeeRepository employeeRepository;

    public void createSupplier(Supplier supplier) {
        validateSupplier(supplier);
        this.supplierRepository.save(supplier);
    }
    public  void createSuppliers(List<Supplier> suppliers){
        List<Supplier> supplierList= new ArrayList<>();
        for (Supplier supplier: suppliers){
            validateSupplier(supplier);
            supplierList.add(supplier);
        }
        this.supplierRepository.saveAll(supplierList);
    }

    //This method allows to consult info of one existing supplier
    public Optional getSupplierById(Long id){
        Optional optionalSupplier = this.supplierRepository.findById(id);
        if(!optionalSupplier.isPresent()){
            throw new ApiRequestException("Supplier not found, try again with a valid id");
        }
        return optionalSupplier;
    }

    //This method allows the user to see a List of existing Suppliers
    public List<Supplier> getAllSuppliers(){
        List<Supplier> suppliers = this.supplierRepository.findAll();
        if(suppliers.isEmpty()){
            throw new ApiRequestException("There is no Suppliers to show, try saving one");
        }
        return suppliers;
    }

    //Method defined to delete supplier, if not found throw a supplier exception
    public void deleteSupplier(Long id){
        Optional optionalSupplier = this.supplierRepository.findById(id);
        if(!optionalSupplier.isPresent()){
            throw new ApiRequestException("Supplier not found, try again with a valid id");
        }
        this.supplierRepository.deleteById(id);
    }

    //Method defined to update Supplier information,
    public Supplier updateSupplier(Supplier supplier){
        Optional<Supplier> optionalSupplier = this.supplierRepository.findById(supplier.getId());
        if(!optionalSupplier.isPresent()){ //If supplier is not found by id, throw exception
            throw new ApiRequestException("Supplier not found, try again with a valid id");
        } //If supplier is found, set new information
        Supplier existingSupplier = optionalSupplier.get();
        existingSupplier.setName(supplier.getName());
        existingSupplier.setAddress(supplier.getAddress());
        existingSupplier.setPhoneNumber(supplier.getPhoneNumber());
        existingSupplier.setEmail(supplier.getEmail());
        existingSupplier.setWebsite(supplier.getWebsite());
        existingSupplier.setIndustrySector(supplier.getIndustrySector());
        existingSupplier.setRegistrationDate(supplier.getRegistrationDate());

        return this.supplierRepository.save(existingSupplier);
    }

    //This method validates if the info sent to create a new Supplier is valid
    public void validateSupplier(Supplier supplier){
        if (supplier.getName() == null || supplier.getName().isEmpty() || supplier.getName().length() < 2) {
            throw new ApiRequestException("Name not valid, check field");
        }
        if (supplier.getAddress() == null || supplier.getAddress().isEmpty()) {
            throw new ApiRequestException("Address not valid, check field");
        }
        if (supplier.getPhoneNumber() == null || supplier.getPhoneNumber().isEmpty() || supplier.getPhoneNumber().length() < 10) {
            throw new ApiRequestException("Phone number not valid, check field");
        }
        if (supplier.getEmail() == null ||supplier.getEmail().isEmpty() || !supplier.getEmail().contains("@")) {
            throw new ApiRequestException("Email not valid, check field");
        }
        if (supplierRepository.findAll().stream()
                .anyMatch(existingSupplier -> existingSupplier.getEmail().equals(supplier.getEmail()))){
            throw new ApiRequestException("Supplier Already exists");
        }
        if (supplier.getWebsite() == null || supplier.getWebsite().isEmpty() ||
                !(supplier.getWebsite().startsWith("www") || supplier.getWebsite().startsWith("http") || supplier.getWebsite().startsWith("https"))) {
            throw new ApiRequestException("Website not valid, check field");
        }
        if (supplier.getIndustrySector() == null || supplier.getIndustrySector().isEmpty()) {
            throw new ApiRequestException("Industry sector not valid, check field");
        }
        if (supplier.getRegistrationDate() == null || supplier.getRegistrationDate().isBefore(LocalDate.of(2000, 1, 1))
                || supplier.getRegistrationDate().isAfter(LocalDate.of(2024, 8, 1))) {
            throw new ApiRequestException("Registration date not valid, check field");
        }
        if (supplier.getEmployee() == null || supplier.getEmployee().getId() == null) {
            throw new ApiRequestException("Employee not valid, check field");
        }
        Optional<Employee> optionalEmployee = employeeRepository.findById(supplier.getEmployee().getId());
        if (!optionalEmployee.isPresent()) {
            throw new ApiRequestException("Employee does not exist");
        }

        Employee employee = optionalEmployee.get();
        if (employee.getRole() == null || !Objects.equals(employee.getRole().getName(), "Coordinator")) {
            throw new ApiRequestException("Employee must have a Coordinator Role");
        }
    }

}
