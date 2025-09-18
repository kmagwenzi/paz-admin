package com.paz.admin.repository;

import com.paz.admin.entity.Prison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrisonRepository extends JpaRepository<Prison, Long> {
    boolean existsByName(String name);
    boolean existsByContactEmail(String contactEmail);
}