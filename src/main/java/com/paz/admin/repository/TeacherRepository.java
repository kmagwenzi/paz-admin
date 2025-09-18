package com.paz.admin.repository;

import com.paz.admin.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    boolean existsByEmail(String email);
    List<Teacher> findByPrisonId(Long prisonId);
    List<Teacher> findBySpecialization(String specialization);
}