package com.paz.admin.controller;

import com.paz.admin.entity.Prison;
import com.paz.admin.entity.Teacher;
import com.paz.admin.payload.response.ErrorResponse;
import com.paz.admin.payload.response.MessageResponse;
import com.paz.admin.repository.PrisonRepository;
import com.paz.admin.repository.TeacherRepository;
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

class TeacherControllerTest {

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private PrisonRepository prisonRepository;

    @InjectMocks
    private TeacherController teacherController;

    private Teacher teacher;
    private Prison prison;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        prison = new Prison();
        prison.setId(1L);
        prison.setName("Harare Central Prison");
        prison.setLocation("Harare");
        prison.setCapacity(500);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setEmail("john.doe@example.com");
        teacher.setPhoneNumber("+263771234567");
        teacher.setSpecialization("Mathematics");
        teacher.setYearsOfExperience(5);
        teacher.setPrison(prison);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getAllTeachers_ShouldReturnListOfTeachers() {
        // Arrange
        List<Teacher> teachers = Arrays.asList(teacher);
        when(teacherRepository.findAll()).thenReturn(teachers);

        // Act
        List<Teacher> result = teacherController.getAllTeachers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(teacher.getEmail(), result.get(0).getEmail());
        verify(teacherRepository, times(1)).findAll();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getTeacherById_WhenTeacherExists_ShouldReturnTeacher() {
        // Arrange
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // Act
        ResponseEntity<Teacher> response = teacherController.getTeacherById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(teacher.getEmail(), response.getBody().getEmail());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getTeacherById_WhenTeacherNotFound_ShouldReturnNotFound() {
        // Arrange
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Teacher> response = teacherController.getTeacherById(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(teacherRepository, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createTeacher_WithValidData_ShouldCreateTeacher() {
        // Arrange
        when(teacherRepository.existsByEmail(teacher.getEmail())).thenReturn(false);
        when(prisonRepository.existsById(prison.getId())).thenReturn(true);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(teacher);

        // Act
        ResponseEntity<?> response = teacherController.createTeacher(teacher);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Teacher);
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createTeacher_WithDuplicateEmail_ShouldReturnConflict() {
        // Arrange
        when(teacherRepository.existsByEmail(teacher.getEmail())).thenReturn(true);

        // Act
        ResponseEntity<?> response = teacherController.createTeacher(teacher);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Email is already in use", error.getMessage());
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createTeacher_WithInvalidEmail_ShouldReturnBadRequest() {
        // Arrange
        teacher.setEmail("invalid-email");

        // Act
        ResponseEntity<?> response = teacherController.createTeacher(teacher);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Invalid email format", error.getMessage());
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void createTeacher_WithNonExistentPrison_ShouldReturnNotFound() {
        // Arrange
        when(teacherRepository.existsByEmail(teacher.getEmail())).thenReturn(false);
        when(prisonRepository.existsById(prison.getId())).thenReturn(false);

        // Act
        ResponseEntity<?> response = teacherController.createTeacher(teacher);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Prison not found", error.getMessage());
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateTeacher_WithValidData_ShouldUpdateTeacher() {
        // Arrange
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setFirstName("Jane");
        updatedTeacher.setLastName("Smith");
        updatedTeacher.setEmail("jane.smith@example.com");
        updatedTeacher.setPhoneNumber("+263772345678");
        updatedTeacher.setSpecialization("Science");
        updatedTeacher.setYearsOfExperience(7);
        updatedTeacher.setPrison(prison);

        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(teacherRepository.existsByEmail(updatedTeacher.getEmail())).thenReturn(false);
        when(prisonRepository.existsById(prison.getId())).thenReturn(true);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(updatedTeacher);

        // Act
        ResponseEntity<?> response = teacherController.updateTeacher(1L, updatedTeacher);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Teacher);
        Teacher result = (Teacher) response.getBody();
        assertEquals("Jane", result.getFirstName());
        verify(teacherRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void updateTeacher_WhenTeacherNotFound_ShouldReturnNotFound() {
        // Arrange
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = teacherController.updateTeacher(1L, teacher);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Teacher not found with id: 1", error.getMessage());
        verify(teacherRepository, never()).save(any(Teacher.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteTeacher_WhenTeacherExists_ShouldDeleteTeacher() {
        // Arrange
        when(teacherRepository.existsById(1L)).thenReturn(true);
        doNothing().when(teacherRepository).deleteById(1L);

        // Act
        ResponseEntity<?> response = teacherController.deleteTeacher(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse message = (MessageResponse) response.getBody();
        assertEquals("Teacher deleted successfully", message.getMessage());
        verify(teacherRepository, times(1)).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void deleteTeacher_WhenTeacherNotFound_ShouldReturnNotFound() {
        // Arrange
        when(teacherRepository.existsById(1L)).thenReturn(false);

        // Act
        ResponseEntity<?> response = teacherController.deleteTeacher(1L);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse error = (ErrorResponse) response.getBody();
        assertEquals("Teacher not found with id: 1", error.getMessage());
        verify(teacherRepository, never()).deleteById(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getTeachersByPrison_ShouldReturnTeachers() {
        // Arrange
        List<Teacher> teachers = Arrays.asList(teacher);
        when(teacherRepository.findByPrisonId(1L)).thenReturn(teachers);

        // Act
        List<Teacher> result = teacherController.getTeachersByPrison(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(teacher.getEmail(), result.get(0).getEmail());
        verify(teacherRepository, times(1)).findByPrisonId(1L);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getTeachersBySpecialization_ShouldReturnTeachers() {
        // Arrange
        List<Teacher> teachers = Arrays.asList(teacher);
        when(teacherRepository.findBySpecialization("Mathematics")).thenReturn(teachers);

        // Act
        List<Teacher> result = teacherController.getTeachersBySpecialization("Mathematics");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(teacher.getEmail(), result.get(0).getEmail());
        verify(teacherRepository, times(1)).findBySpecialization("Mathematics");
    }
}