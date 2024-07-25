package com.IntegrativeProject.ActionFactory.controller;

import com.IntegrativeProject.ActionFactory.model.Role;
import com.IntegrativeProject.ActionFactory.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping()
    public ResponseEntity<String> createRole(@RequestBody Role role){
        this.roleService.createRole(role);
        return  ResponseEntity.ok("Role created");
    }
    @GetMapping()
    public List<Role> seeRoles(){
        return  this.roleService.seeRoles();
    }
}
