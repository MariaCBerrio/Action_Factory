package com.IntegrativeProject.ActionFactory.controller;

import com.IntegrativeProject.ActionFactory.Exceptions.ApiRequestException;
import com.IntegrativeProject.ActionFactory.model.Supplier;
import com.IntegrativeProject.ActionFactory.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    @Autowired
    public SupplierController(SupplierService supplierService){
        this.supplierService = supplierService;
    }
    @PostMapping()
    public ResponseEntity<String> createSupplier(@RequestBody Supplier supplier) {
        try {
            this.supplierService.createSupplier(supplier);
            return ResponseEntity.status(HttpStatus.CREATED).body("Supplier created successfully");
        }catch (ApiRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/list")
    public ResponseEntity<String> createSuppliers(@RequestBody List<Supplier>  suppliers) {
        supplierService.createSuppliers(suppliers);
        return ResponseEntity.status(HttpStatus.CREATED).body("Suppliers created successfully");
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(this.supplierService.getSupplierById(id));
        } catch (ApiRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllSuppliers(){
        try {
            return ResponseEntity.ok(this.supplierService.getAllSuppliers());
        }catch(ApiRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteSupplier(@PathVariable("id") Long id){
        try{
            this.supplierService.deleteSupplier(id);
            return ResponseEntity.ok("Supplier deleted successfully");
        }catch (ApiRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PutMapping("{id}")
    public ResponseEntity<String> updateSupplier(@PathVariable("id") Long id, @RequestBody Supplier supplier){
        supplier.setId(id);
        try {
            Supplier updatedSupplier = this.supplierService.updateSupplier(supplier);
            return ResponseEntity.status(HttpStatus.CREATED).body("Supplier updated successfully");
        }catch (ApiRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}

