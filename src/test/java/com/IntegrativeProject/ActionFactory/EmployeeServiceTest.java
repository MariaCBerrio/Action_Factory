package com.IntegrativeProject.ActionFactory;

import com.IntegrativeProject.ActionFactory.Exceptions.ApiRequestException;
import com.IntegrativeProject.ActionFactory.model.Employee;
import com.IntegrativeProject.ActionFactory.model.Role;
import com.IntegrativeProject.ActionFactory.repository.EmployeeRepository;
import com.IntegrativeProject.ActionFactory.service.EmployeeService;
import com.IntegrativeProject.ActionFactory.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmployeeServiceTest {

    private EmployeeService employeeService;
    private EmployeeRepository employeeRepository;
    private RoleService roleService;

    private Employee employee;
    private Role role;

    @BeforeEach
    public void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        roleService = mock(RoleService.class);

        employeeService = new EmployeeService(employeeRepository, roleService);

        role = new Role("Coordinator");
        role.setId(1L);
        employee = new Employee(123456789L, "Eimy", "eimy.gb@example.com", "Password123",
                LocalDate.now(), LocalDateTime.now(), "Active", role);
    }


    @Test
    public void createEmployeeSuccess() {
        when(roleService.findById(any(Long.class))).thenReturn(Optional.of(role));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        assertDoesNotThrow(() -> employeeService.createEmployee(employee));
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    public void createEmployeeRoleNotFound() {
        when(roleService.findById(any(Long.class))).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> employeeService.createEmployee(employee));
        assertEquals("Role not found", exception.getMessage());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void createEmployeesSuccess() {
        when(roleService.findById(any(Long.class))).thenReturn(Optional.of(role));
        when(employeeRepository.saveAll(anyList())).thenReturn(Arrays.asList(employee));

        assertDoesNotThrow(() -> employeeService.createEmployees(Arrays.asList(employee)));
        verify(employeeRepository, times(1)).saveAll(Arrays.asList(employee));
    }

    @Test
    public void  createEmployeesRoleNotFound() {
        when(roleService.findById(any(Long.class))).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> employeeService.createEmployees(Arrays.asList(employee)));
        assertTrue(exception.getMessage().contains("Role not found for employee"));
        verify(employeeRepository, never()).saveAll(anyList());
    }

    @Test
    public void testSeeEmployees() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee));

        List<Employee> employees = employeeService.seeEmployees();
        assertFalse(employees.isEmpty());
        assertEquals(1, employees.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    public void testSeeEmployeesNoData() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList());

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> employeeService.seeEmployees());
        assertEquals("There are no employees to show, try saving one", exception.getMessage());
        verify(employeeRepository, times(1)).findAll();
    }


    @Test
    public void testDeleteEmployeeByIdNotFound() {
        when(employeeRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        ApiRequestException exception = assertThrows(ApiRequestException.class, () -> employeeService.deleteEmployeeById(employee.getId()));
        assertEquals("Employee with ID: " + employee.getId() + " not found", exception.getMessage());
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}

