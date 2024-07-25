package com.IntegrativeProject.ActionFactory.controller;

import com.IntegrativeProject.ActionFactory.model.Employee;
import com.IntegrativeProject.ActionFactory.service.EmployeeService;
import com.IntegrativeProject.ActionFactory.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/employees")
public class EmployeeController {

    private EmployeeService employeeService;
    private RoleService roleService;


    @Autowired
    public EmployeeController(EmployeeService employeeService, RoleService roleService) {
        this.employeeService = employeeService;
        this.roleService = roleService;
    }

    @PostMapping()
    public ResponseEntity<String> createEmployee(@RequestBody Employee employee) {
            employeeService.createEmployee(employee);
            return ResponseEntity.status(HttpStatus.CREATED).body("Employee created successfully");
    }

    @PostMapping("/list")
    public ResponseEntity<String> createEmployees(@RequestBody List<Employee>  employees) {
            employeeService.createEmployees(employees);
            return ResponseEntity.status(HttpStatus.CREATED).body("Employees created successfully");
    }

    @GetMapping()
    public List<Employee> seeEmployees(){
        return  this.employeeService.seeEmployees();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") Long id){
        this.employeeService.deleteEmployeeById(id);
        return ResponseEntity.status(HttpStatus.CREATED).body("employee successfully deleted");
    }


}
