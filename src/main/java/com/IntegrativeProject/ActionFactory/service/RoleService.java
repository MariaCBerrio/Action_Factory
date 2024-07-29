package com.IntegrativeProject.ActionFactory.service;

import com.IntegrativeProject.ActionFactory.model.Role;
import com.IntegrativeProject.ActionFactory.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final Logger logger = Logger.getLogger(RoleService.class.getName());

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void createRole(Role role) {
        logger.info("Creating new role: " + role.getName());
        // Add validation logic here
        if (role != null && role.getName() != null && !role.getName().isEmpty()) {
            roleRepository.save(role);
        } else {
            logger.warning("Invalid role object: " + role);
            throw new IllegalArgumentException("Invalid role data");
        }
    }

    public List<Role> seeRoles() {
        logger.info("Fetching all roles");
        return roleRepository.findAll();
    }

    public Optional<Role> findById(Long id) {
        logger.info("Fetching role by id: " + id);
        Optional<Role> role = roleRepository.findById(id);
        if (role.isEmpty()) {
            throw new RoleNotFoundException("Role not found with id: " + id);
        }
        return role;
    }

    @Transactional
    public void deleteRoleById(Long id) {
        logger.info("Deleting role by id: " + id);
        if (roleRepository.existsById(id)) {
            roleRepository.deleteById(id);
        } else {
            logger.warning("Role not found with id: " + id);
            throw new RoleNotFoundException("Role not found with id: " + id);
        }
    }

    // Custom exception class
    public static class RoleNotFoundException extends RuntimeException {
        public RoleNotFoundException(String message) {
            super(message);
        }
    }
}
