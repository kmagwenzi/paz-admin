package com.paz.admin.controller;

import com.paz.admin.entity.Teacher;
import com.paz.admin.payload.response.ErrorResponse;
import com.paz.admin.payload.response.MessageResponse;
import com.paz.admin.repository.PrisonRepository;
import com.paz.admin.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private PrisonRepository prisonRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRISON_MANAGER')")
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRISON_MANAGER')")
    public ResponseEntity<Teacher> getTeacherById(@PathVariable Long id) {
        Optional<Teacher> teacher = teacherRepository.findById(id);
        return teacher.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/prison/{prisonId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRISON_MANAGER')")
    public List<Teacher> getTeachersByPrison(@PathVariable Long prisonId) {
        return teacherRepository.findByPrisonId(prisonId);
    }

    @GetMapping("/specialization/{specialization}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRISON_MANAGER')")
    public List<Teacher> getTeachersBySpecialization(@PathVariable String specialization) {
        return teacherRepository.findBySpecialization(specialization);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRISON_MANAGER')")
    public ResponseEntity<?> createTeacher(@Valid @RequestBody Teacher teacher) {
        try {
            // Validate required fields
            if (teacher.getFirstName() == null || teacher.getFirstName().trim().isEmpty()) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "First name is required");
            }
            if (teacher.getLastName() == null || teacher.getLastName().trim().isEmpty()) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Last name is required");
            }
            if (teacher.getEmail() == null || teacher.getEmail().trim().isEmpty()) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Email is required");
            }

            // Validate email format
            if (!isValidEmail(teacher.getEmail())) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid email format");
            }

            if (teacherRepository.existsByEmail(teacher.getEmail())) {
                return createErrorResponse(HttpStatus.CONFLICT, "Email is already in use");
            }

            // Validate prison exists if provided
            if (teacher.getPrison() != null && teacher.getPrison().getId() != null) {
                if (!prisonRepository.existsById(teacher.getPrison().getId())) {
                    return createErrorResponse(HttpStatus.NOT_FOUND, "Prison not found");
                }
            }

            Teacher savedTeacher = teacherRepository.save(teacher);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTeacher);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create teacher: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRISON_MANAGER')")
    public ResponseEntity<?> updateTeacher(@PathVariable Long id, @Valid @RequestBody Teacher teacherDetails) {
        try {
            Optional<Teacher> optionalTeacher = teacherRepository.findById(id);
            if (optionalTeacher.isEmpty()) {
                return createErrorResponse(HttpStatus.NOT_FOUND, "Teacher not found with id: " + id);
            }

            Teacher teacher = optionalTeacher.get();

            // Validate required fields
            if (teacherDetails.getFirstName() == null || teacherDetails.getFirstName().trim().isEmpty()) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "First name is required");
            }
            if (teacherDetails.getLastName() == null || teacherDetails.getLastName().trim().isEmpty()) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Last name is required");
            }
            if (teacherDetails.getEmail() == null || teacherDetails.getEmail().trim().isEmpty()) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Email is required");
            }

            // Validate email format
            if (!isValidEmail(teacherDetails.getEmail())) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid email format");
            }

            // Check if email is being changed and if new email already exists
            if (!teacher.getEmail().equals(teacherDetails.getEmail()) &&
                teacherRepository.existsByEmail(teacherDetails.getEmail())) {
                return createErrorResponse(HttpStatus.CONFLICT, "Email is already in use");
            }

            // Validate prison exists if provided
            if (teacherDetails.getPrison() != null && teacherDetails.getPrison().getId() != null) {
                if (!prisonRepository.existsById(teacherDetails.getPrison().getId())) {
                    return createErrorResponse(HttpStatus.NOT_FOUND, "Prison not found");
                }
            }

            teacher.setFirstName(teacherDetails.getFirstName());
            teacher.setLastName(teacherDetails.getLastName());
            teacher.setEmail(teacherDetails.getEmail());
            teacher.setPhoneNumber(teacherDetails.getPhoneNumber());
            teacher.setSpecialization(teacherDetails.getSpecialization());
            teacher.setYearsOfExperience(teacherDetails.getYearsOfExperience());
            teacher.setPrison(teacherDetails.getPrison());

            Teacher updatedTeacher = teacherRepository.save(teacher);
            return ResponseEntity.ok(updatedTeacher);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update teacher: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTeacher(@PathVariable Long id) {
        try {
            if (!teacherRepository.existsById(id)) {
                return createErrorResponse(HttpStatus.NOT_FOUND, "Teacher not found with id: " + id);
            }

            teacherRepository.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("Teacher deleted successfully"));
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete teacher: " + e.getMessage());
        }
    }

    // Helper methods
    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message) {
        String path = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getRequestURI();
        return ResponseEntity.status(status).body(new ErrorResponse(status.value(), message, path));
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }
}