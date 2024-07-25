package com.IntegrativeProject.ActionFactory.repository;

import com.IntegrativeProject.ActionFactory.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {

}
