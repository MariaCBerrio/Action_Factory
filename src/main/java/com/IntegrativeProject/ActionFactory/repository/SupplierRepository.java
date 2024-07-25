package com.IntegrativeProject.ActionFactory.repository;

import com.IntegrativeProject.ActionFactory.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
