package com.IntegrativeProject.ActionFactory.service;

import com.IntegrativeProject.ActionFactory.model.Role;
import com.IntegrativeProject.ActionFactory.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public  void createRole(Role role){
        this.roleRepository.save(role);
    }

    public List<Role> seeRoles(){
        return  this.roleRepository.findAll();
    }

    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }
}
