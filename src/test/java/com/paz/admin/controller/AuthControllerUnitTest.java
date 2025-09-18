package com.paz.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paz.admin.config.JwtUtils;
import com.paz.admin.entity.Role;
import com.paz.admin.entity.User;
import com.paz.admin.payload.request.LoginRequest;
import com.paz.admin.payload.request.SignupRequest;
import com.paz.admin.payload.response.ErrorResponse;
import com.paz.admin.payload.response.JwtResponse;
import com.paz.admin.payload.response.MessageResponse;
import com.paz.admin.payload.response.UserProfileResponse;
import com.paz.admin.repository.RoleRepository;
import com.paz.admin.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerUnitTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    private LoginRequest loginRequest;
    private SignupRequest signupRequest;
    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setRole(Set.of("teacher"));

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        userRole = new Role();
        userRole.setId(1L);
        userRole.setName("ROLE_TEACHER");
    }

    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnJwtResponse() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwtToken");
        when(authentication.getPrincipal()).thenReturn(new org.springframework.security.core.userdetails.User(
                "testuser", "password", Collections.emptyList()));

        // Act
        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof JwtResponse);
        JwtResponse jwtResponse = (JwtResponse) response.getBody();
        assertEquals("jwtToken", jwtResponse.getAccessToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
    }

    @Test
    void authenticateUser_WithInvalidCredentials_ShouldThrowException() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> {
            authController.authenticateUser(loginRequest);
        });
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void registerUser_WithNewUser_ShouldReturnSuccessMessage() throws Exception {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_TEACHER")).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("User registered successfully!", messageResponse.getMessage());
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(roleRepository, times(1)).findByName("ROLE_TEACHER");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_WithDuplicateUsername_ShouldReturnError() throws Exception {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Error: Username is already taken!", errorResponse.getMessage());
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithDuplicateEmail_ShouldReturnError() throws Exception {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.registerUser(signupRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Error: Email is already in use!", errorResponse.getMessage());
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithInvalidRole_ShouldThrowException() throws Exception {
        // Arrange
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(roleRepository.findByName("ROLE_TEACHER")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            authController.registerUser(signupRequest);
        });
        verify(userRepository, times(1)).existsByUsername("testuser");
        verify(userRepository, times(1)).existsByEmail("test@example.com");
        verify(roleRepository, times(1)).findByName("ROLE_TEACHER");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void validateToken_ShouldReturnSuccessMessage() throws Exception {
        // Act
        ResponseEntity<?> response = authController.validateToken();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof MessageResponse);
        MessageResponse messageResponse = (MessageResponse) response.getBody();
        assertEquals("Token is valid", messageResponse.getMessage());
    }

    @Test
    void getUserProfile_WhenUserExists_ShouldReturnUserProfile() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(new org.springframework.security.core.userdetails.User(
                "testuser", "password", Collections.emptyList()) {
            public Long getId() { return 1L; }
        });
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        testUser.setRoles(Set.of(userRole));

        // Act
        ResponseEntity<?> response = authController.getUserProfile();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof UserProfileResponse);
        UserProfileResponse profileResponse = (UserProfileResponse) response.getBody();
        assertEquals("testuser", profileResponse.getUsername());
        assertEquals("test@example.com", profileResponse.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserProfile_WhenUserNotFound_ShouldReturnError() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.getPrincipal()).thenReturn(new org.springframework.security.core.userdetails.User(
                "testuser", "password", Collections.emptyList()) {
            public Long getId() { return 1L; }
        });
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = authController.getUserProfile();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertTrue(errorResponse.getMessage().contains("Failed to get user profile"));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getAllUsers_AsAdmin_ShouldReturnListOfUsers() throws Exception {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        testUser.setRoles(Set.of(userRole));

        // Act
        ResponseEntity<?> response = authController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<UserProfileResponse> userResponses = (List<UserProfileResponse>) response.getBody();
        assertEquals(1, userResponses.size());
        assertEquals("testuser", userResponses.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_WhenRepositoryThrowsException_ShouldReturnError() throws Exception {
        // Arrange
        when(userRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = authController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorResponse);
        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertTrue(errorResponse.getMessage().contains("Failed to get users"));
        verify(userRepository, times(1)).findAll();
    }
}