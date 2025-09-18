package com.paz.admin.controller;

import com.paz.admin.entity.Prison;
import com.paz.admin.payload.response.ErrorResponse;
import com.paz.admin.payload.response.MessageResponse;
import com.paz.admin.repository.PrisonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PrisonControllerTest {

    @Mock
    private PrisonRepository prisonRepository;

    @InjectMocks
    private PrisonController prisonController;

    private Prison prison;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        prison = new Prison();
        prison.setId(1L);
        prison.setName("Harare Central Prison");
        prison.setLocation("Harare CBD");
        prison.setCapacity(500);
        prison.setCurrentPopulation(250);
        prison.setContactEmail("harare.prison@justice.gov.zw");
        prison.setContactPhone("+263242123456");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllPrisons_ShouldReturnListOfPrisons() {
        // Arrange
        List<Prison> prisons = Arrays.asList(prison);
        when(prisonRepository.findAll()).thenReturn(prisons);

        // Act
        List<Prison> result = prisonController.getAllPrisons();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(prison.getName(), result.get(0).getName());
        verify(prisonRepository, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getPrisonById_WhenPrisonExists_ShouldReturnPrison() {
        // Arrange
        when(prisonRepository.findById(1L)).thenReturn(Optional.of(prison));

        // Act
        ResponseEntity<Prison> response = prisonController.getPrisonById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(prison.getName(), response.getBody().getName());
        verify(prisonRepository, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getPrisonById_WhenPrisonNotFound_ShouldReturnNotFound() {
        // Arrange
        when(prisonRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Prison> response = prisonController.getPrisonById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(prisonRepository, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createPrison_WithValidData_ShouldCreatePrison() {
        // Arrange
        when(prisonRepository.existsByName(prison.getName())).thenReturn(false);
        when(prisonRepository.existsByContactEmail(prison.getContactEmail())).thenReturn(false);
        when(prisonRepository.save(any(Prison.class))).thenReturn(prison);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(prison);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Prison);
        verify(prisonRepository, times(1)).save(any(Prison.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createPrison_WithDuplicateName_ShouldReturnConflict() {
        // Arrange
        when(prisonRepository.existsByName(prison.getName())).thenReturn(true);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(prison);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Prison name is already taken", error.getMessage());
        verify(prisonRepository, never()).save(any(Prison.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createPrison_WithDuplicateEmail_ShouldReturnConflict() {
        // Arrange
        when(prisonRepository.existsByName(prison.getName())).thenReturn(false);
        when(prisonRepository.existsByContactEmail(prison.getContactEmail())).thenReturn(true);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(prison);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Contact email is already in use", error.getMessage());
        verify(prisonRepository, never()).save(any(Prison.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createPrison_WithInvalidEmail_ShouldReturnBadRequest() {
        // Arrange
        prison.setContactEmail("invalid-email");

        // Act
        ResponseEntity<?> response = prisonController.createPrison(prison);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Invalid contact email format", error.getMessage());
        verify(prisonRepository, never()).save(any(Prison.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createPrison_WithInvalidCapacity_ShouldReturnBadRequest() {
        // Arrange
        prison.setCapacity(0);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(prison);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Capacity must be greater than 0", error.getMessage());
        verify(prisonRepository, never()).save(any(Prison.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createPrison_WithNegativePopulation_ShouldReturnBadRequest() {
        // Arrange
        prison.setCurrentPopulation(-1);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(prison);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Current population cannot be negative", error.getMessage());
        verify(prisonRepository, never()).save(any(Prison.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updatePrison_WithValidData_ShouldUpdatePrison() {
        // Arrange
        Prison updatedPrison = new Prison();
        updatedPrison.setName("Harare Maximum Security Prison");
        updatedPrison.setLocation("Harare CBD");
        updatedPrison.setCapacity(600);
        updatedPrison.setCurrentPopulation(300);
        updatedPrison.setContactEmail("max.security@justice.gov.zw");
        updatedPrison.setContactPhone("+263242654321");

        when(prisonRepository.findById(1L)).thenReturn(Optional.of(prison));
        when(prisonRepository.existsByName(updatedPrison.getName())).thenReturn(false);
        when(prisonRepository.existsByContactEmail(updatedPrison.getContactEmail())).thenReturn(false);
        when(prisonRepository.save(any(Prison.class))).thenReturn(updatedPrison);

        // Act
        ResponseEntity<?> response = prisonController.updatePrison(1L, updatedPrison);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Prison);
        Prison result = (Prison) response.getBody();
        assertEquals("Harare Maximum Security Prison", result.getName());
        verify(prisonRepository, times(1)).save(any(Prison.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updatePrison_WhenPrisonNotFound_ShouldReturnNotFound() {
        // Arrange
        when(prisonRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = prisonController.updatePrison(1L, prison);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Prison not found with id: 1", error.getMessage());
        verify(prisonRepository, never()).save(any(Prison.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updatePrison_WithDuplicateName_ShouldReturnConflict() {
        // Arrange
        Prison updatedPrison = new Prison();
        updatedPrison.setName("Duplicate Prison Name");

        when(prisonRepository.findById(1L)).thenReturn(Optional.of(prison));
        when(prisonRepository.existsByName(updatedPrison.getName())).thenReturn(true);

        // Act
        ResponseEntity<?> response = prisonController.updatePrison(1L, updatedPrison);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Prison name is already taken", error.getMessage());
        verify(prisonRepository, never()).save(any(Prison.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deletePrison_WhenPrisonExists_ShouldDeletePrison() {
        // Arrange
        when(prisonRepository.existsById(1L)).thenReturn(true);
        doNothing().when(prisonRepository).deleteById(1L);

        // Act
        ResponseEntity<?> response = prisonController.deletePrison(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse message = (MessageResponse) response.getBody();
        assertEquals("Prison deleted successfully", message.getMessage());
        verify(prisonRepository, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deletePrison_WhenPrisonNotFound_ShouldReturnNotFound() {
        // Arrange
        when(prisonRepository.existsById(1L)).thenReturn(false);

        // Act
        ResponseEntity<?> response = prisonController.deletePrison(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Prison not found with id: 1", error.getMessage());
        verify(prisonRepository, never()).deleteById(1L);
    }
}