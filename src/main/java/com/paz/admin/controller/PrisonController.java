package com.paz.admin.controller;

import com.paz.admin.entity.Prison;
import com.paz.admin.payload.response.ErrorResponse;
import com.paz.admin.payload.response.MessageResponse;
import com.paz.admin.repository.PrisonRepository;
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
@RequestMapping("/api/prisons")
public class PrisonController {

    @Autowired
    private PrisonRepository prisonRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRISON_MANAGER')")
    public List<Prison> getAllPrisons() {
        return prisonRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PRISON_MANAGER')")
    public ResponseEntity<Prison> getPrisonById(@PathVariable Long id) {
        Optional<Prison> prison = prisonRepository.findById(id);
        return prison.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createPrison(@Valid @RequestBody Prison prison) {
        try {
            // Validate required fields
            if (prison.getName() == null || prison.getName().trim().isEmpty()) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Prison name is required");
            }
            if (prison.getLocation() == null || prison.getLocation().trim().isEmpty()) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Location is required");
            }

            // Validate capacity
            if (prison.getCapacity() != null && prison.getCapacity() <= 0) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Capacity must be greater than 0");
            }

            // Validate current population
            if (prison.getCurrentPopulation() != null && prison.getCurrentPopulation() < 0) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Current population cannot be negative");
            }

            if (prisonRepository.existsByName(prison.getName())) {
                return createErrorResponse(HttpStatus.CONFLICT, "Prison name is already taken");
            }

            if (prison.getContactEmail() != null && !prison.getContactEmail().trim().isEmpty()) {
                if (!isValidEmail(prison.getContactEmail())) {
                    return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid contact email format");
                }
                if (prisonRepository.existsByContactEmail(prison.getContactEmail())) {
                    return createErrorResponse(HttpStatus.CONFLICT, "Contact email is already in use");
                }
            }

            Prison savedPrison = prisonRepository.save(prison);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPrison);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create prison: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updatePrison(@PathVariable Long id, @Valid @RequestBody Prison prisonDetails) {
        try {
            Optional<Prison> optionalPrison = prisonRepository.findById(id);
            if (optionalPrison.isEmpty()) {
                return createErrorResponse(HttpStatus.NOT_FOUND, "Prison not found with id: " + id);
            }

            Prison prison = optionalPrison.get();

            // Validate required fields
            if (prisonDetails.getName() == null || prisonDetails.getName().trim().isEmpty()) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Prison name is required");
            }
            if (prisonDetails.getLocation() == null || prisonDetails.getLocation().trim().isEmpty()) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Location is required");
            }

            // Validate capacity
            if (prisonDetails.getCapacity() != null && prisonDetails.getCapacity() <= 0) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Capacity must be greater than 0");
            }

            // Validate current population
            if (prisonDetails.getCurrentPopulation() != null && prisonDetails.getCurrentPopulation() < 0) {
                return createErrorResponse(HttpStatus.BAD_REQUEST, "Current population cannot be negative");
            }

            // Check if name is being changed and if new name already exists
            if (!prison.getName().equals(prisonDetails.getName()) &&
                prisonRepository.existsByName(prisonDetails.getName())) {
                return createErrorResponse(HttpStatus.CONFLICT, "Prison name is already taken");
            }

            // Check if email is being changed and if new email already exists
            if (prisonDetails.getContactEmail() != null && !prisonDetails.getContactEmail().trim().isEmpty()) {
                if (!isValidEmail(prisonDetails.getContactEmail())) {
                    return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid contact email format");
                }
                if (!prisonDetails.getContactEmail().equals(prison.getContactEmail()) &&
                    prisonRepository.existsByContactEmail(prisonDetails.getContactEmail())) {
                    return createErrorResponse(HttpStatus.CONFLICT, "Contact email is already in use");
                }
            }

            prison.setName(prisonDetails.getName());
            prison.setLocation(prisonDetails.getLocation());
            prison.setCapacity(prisonDetails.getCapacity());
            prison.setCurrentPopulation(prisonDetails.getCurrentPopulation());
            prison.setContactEmail(prisonDetails.getContactEmail());
            prison.setContactPhone(prisonDetails.getContactPhone());

            Prison updatedPrison = prisonRepository.save(prison);
            return ResponseEntity.ok(updatedPrison);
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update prison: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePrison(@PathVariable Long id) {
        try {
            if (!prisonRepository.existsById(id)) {
                return createErrorResponse(HttpStatus.NOT_FOUND, "Prison not found with id: " + id);
            }

            prisonRepository.deleteById(id);
            return ResponseEntity.ok(new MessageResponse("Prison deleted successfully"));
        } catch (Exception e) {
            return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete prison: " + e.getMessage());
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