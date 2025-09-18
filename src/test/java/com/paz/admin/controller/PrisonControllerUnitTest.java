package com.paz.admin.controller;

import com.paz.admin.entity.Prison;
import com.paz.admin.payload.response.ErrorResponse;
import com.paz.admin.payload.response.MessageResponse;
import com.paz.admin.repository.PrisonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrisonControllerUnitTest {

    @Mock
    private PrisonRepository prisonRepository;

    @InjectMocks
    private PrisonController prisonController;

    private Prison testPrison;
    private Prison testPrison2;

    @BeforeEach
    void setUp() {
        // Setup request context for error responses
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/prisons");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        testPrison = new Prison();
        testPrison.setId(1L);
        testPrison.setName("Harare Central Prison");
        testPrison.setLocation("Harare CBD");
        testPrison.setCapacity(500);
        testPrison.setCurrentPopulation(250);
        testPrison.setContactEmail("harare@prison.gov.zw");
        testPrison.setContactPhone("+263242123456");

        testPrison2 = new Prison();
        testPrison2.setId(2L);
        testPrison2.setName("Chikurubi Maximum");
        testPrison2.setLocation("Chikurubi");
        testPrison2.setCapacity(1000);
        testPrison2.setCurrentPopulation(800);
    }

    @Test
    void testGetAllPrisons_Success() {
        // Arrange
        when(prisonRepository.findAll()).thenReturn(Arrays.asList(testPrison, testPrison2));

        // Act
        List<Prison> result = prisonController.getAllPrisons();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Harare Central Prison", result.get(0).getName());
        assertEquals("Chikurubi Maximum", result.get(1).getName());
        verify(prisonRepository, times(1)).findAll();
    }

    @Test
    void testGetPrisonById_Success() {
        // Arrange
        when(prisonRepository.findById(1L)).thenReturn(Optional.of(testPrison));

        // Act
        ResponseEntity<Prison> response = prisonController.getPrisonById(1L);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertEquals("Harare Central Prison", response.getBody().getName());
        verify(prisonRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPrisonById_NotFound() {
        // Arrange
        when(prisonRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Prison> response = prisonController.getPrisonById(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(prisonRepository, times(1)).findById(999L);
    }

    @Test
    void testCreatePrison_Success() {
        // Arrange
        when(prisonRepository.existsByName("Harare Central Prison")).thenReturn(false);
        when(prisonRepository.existsByContactEmail("harare@prison.gov.zw")).thenReturn(false);
        when(prisonRepository.save(any(Prison.class))).thenReturn(testPrison);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(testPrison);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Prison);
        Prison createdPrison = (Prison) response.getBody();
        assertEquals("Harare Central Prison", createdPrison.getName());
        verify(prisonRepository, times(1)).existsByName("Harare Central Prison");
        verify(prisonRepository, times(1)).existsByContactEmail("harare@prison.gov.zw");
        verify(prisonRepository, times(1)).save(any(Prison.class));
    }

    @Test
    void testCreatePrison_MissingName() {
        // Arrange
        Prison invalidPrison = new Prison();
        invalidPrison.setLocation("Test Location");
        invalidPrison.setCapacity(100);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(invalidPrison);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Prison name is required", error.getMessage());
        verify(prisonRepository, never()).save(any());
    }

    @Test
    void testCreatePrison_MissingLocation() {
        // Arrange
        Prison invalidPrison = new Prison();
        invalidPrison.setName("Test Prison");
        invalidPrison.setCapacity(100);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(invalidPrison);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Location is required", error.getMessage());
        verify(prisonRepository, never()).save(any());
    }

    @Test
    void testCreatePrison_NegativeCapacity() {
        // Arrange
        Prison invalidPrison = new Prison();
        invalidPrison.setName("Test Prison");
        invalidPrison.setLocation("Test Location");
        invalidPrison.setCapacity(-1);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(invalidPrison);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Capacity must be greater than 0", error.getMessage());
        verify(prisonRepository, never()).save(any());
    }

    @Test
    void testCreatePrison_NegativePopulation() {
        // Arrange
        Prison invalidPrison = new Prison();
        invalidPrison.setName("Test Prison");
        invalidPrison.setLocation("Test Location");
        invalidPrison.setCapacity(100);
        invalidPrison.setCurrentPopulation(-5);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(invalidPrison);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Current population cannot be negative", error.getMessage());
        verify(prisonRepository, never()).save(any());
    }

    @Test
    void testCreatePrison_DuplicateName() {
        // Arrange
        when(prisonRepository.existsByName("Harare Central Prison")).thenReturn(true);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(testPrison);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Prison name is already taken", error.getMessage());
        verify(prisonRepository, times(1)).existsByName("Harare Central Prison");
        verify(prisonRepository, never()).save(any());
    }

    @Test
    void testCreatePrison_InvalidEmail() {
        // Arrange
        Prison invalidPrison = new Prison();
        invalidPrison.setName("Test Prison");
        invalidPrison.setLocation("Test Location");
        invalidPrison.setCapacity(100);
        invalidPrison.setContactEmail("invalid-email");

        when(prisonRepository.existsByName("Test Prison")).thenReturn(false);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(invalidPrison);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Invalid contact email format", error.getMessage());
        verify(prisonRepository, times(1)).existsByName("Test Prison");
        verify(prisonRepository, never()).save(any());
    }

    @Test
    void testCreatePrison_DuplicateEmail() {
        // Arrange
        when(prisonRepository.existsByName("Harare Central Prison")).thenReturn(false);
        when(prisonRepository.existsByContactEmail("harare@prison.gov.zw")).thenReturn(true);

        // Act
        ResponseEntity<?> response = prisonController.createPrison(testPrison);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Contact email is already in use", error.getMessage());
        verify(prisonRepository, times(1)).existsByName("Harare Central Prison");
        verify(prisonRepository, times(1)).existsByContactEmail("harare@prison.gov.zw");
        verify(prisonRepository, never()).save(any());
    }

    @Test
    void testUpdatePrison_Success() {
        // Arrange
        when(prisonRepository.findById(1L)).thenReturn(Optional.of(testPrison));
        when(prisonRepository.existsByName("Updated Prison")).thenReturn(false);
        when(prisonRepository.save(any(Prison.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Prison updatedPrison = new Prison();
        updatedPrison.setName("Updated Prison");
        updatedPrison.setLocation("Updated Location");
        updatedPrison.setCapacity(600);
        updatedPrison.setCurrentPopulation(300);
        updatedPrison.setContactEmail("updated@prison.gov.zw");
        updatedPrison.setContactPhone("+263242999999");

        // Act
        ResponseEntity<?> response = prisonController.updatePrison(1L, updatedPrison);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Prison);
        Prison result = (Prison) response.getBody();
        assertEquals("Updated Prison", result.getName());
        assertEquals("Updated Location", result.getLocation());
        assertEquals(600, result.getCapacity());
        verify(prisonRepository, times(1)).findById(1L);
        verify(prisonRepository, times(1)).existsByName("Updated Prison");
        verify(prisonRepository, times(1)).save(any(Prison.class));
    }

    @Test
    void testUpdatePrison_NotFound() {
        // Arrange
        when(prisonRepository.findById(999L)).thenReturn(Optional.empty());

        Prison updatedPrison = new Prison();
        updatedPrison.setName("Updated Prison");
        updatedPrison.setLocation("Updated Location");
        updatedPrison.setCapacity(600);

        // Act
        ResponseEntity<?> response = prisonController.updatePrison(999L, updatedPrison);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Prison not found with id: 999", error.getMessage());
        verify(prisonRepository, times(1)).findById(999L);
        verify(prisonRepository, never()).save(any());
    }

    @Test
    void testUpdatePrison_DuplicateName() {
        // Arrange
        when(prisonRepository.findById(1L)).thenReturn(Optional.of(testPrison));
        when(prisonRepository.existsByName("Duplicate Prison")).thenReturn(true);

        Prison updatedPrison = new Prison();
        updatedPrison.setName("Duplicate Prison");
        updatedPrison.setLocation("Updated Location");
        updatedPrison.setCapacity(600);

        // Act
        ResponseEntity<?> response = prisonController.updatePrison(1L, updatedPrison);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Prison name is already taken", error.getMessage());
        verify(prisonRepository, times(1)).findById(1L);
        verify(prisonRepository, times(1)).existsByName("Duplicate Prison");
        verify(prisonRepository, never()).save(any());
    }

    @Test
    void testDeletePrison_Success() {
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
        verify(prisonRepository, times(1)).existsById(1L);
        verify(prisonRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeletePrison_NotFound() {
        // Arrange
        when(prisonRepository.existsById(999L)).thenReturn(false);

        // Act
        ResponseEntity<?> response = prisonController.deletePrison(999L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Prison not found with id: 999", error.getMessage());
        verify(prisonRepository, times(1)).existsById(999L);
        verify(prisonRepository, never()).deleteById(any());
    }

    @Test
    void testIsValidEmail_Valid() {
        // Act
        boolean result = prisonController.isValidEmail("test@example.com");

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsValidEmail_Invalid() {
        // Act
        boolean result = prisonController.isValidEmail("invalid-email");

        // Assert
        assertFalse(result);
    }

    @Test
    void testIsValidEmail_Null() {
        // Act
        boolean result = prisonController.isValidEmail(null);

        // Assert
        assertFalse(result);
    }
}