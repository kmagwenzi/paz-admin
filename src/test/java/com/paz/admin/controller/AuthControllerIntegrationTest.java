package com.paz.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paz.admin.entity.Role;
import com.paz.admin.entity.User;
import com.paz.admin.payload.request.LoginRequest;
import com.paz.admin.payload.request.SignupRequest;
import com.paz.admin.repository.RoleRepository;
import com.paz.admin.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create roles
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        roleRepository.save(adminRole);

        Role teacherRole = new Role();
        teacherRole.setName("ROLE_TEACHER");
        roleRepository.save(teacherRole);

        Role managerRole = new Role();
        managerRole.setName("ROLE_PRISON_MANAGER");
        roleRepository.save(managerRole);
    }

    @Test
    void testUserRegistrationAndLogin() throws Exception {
        // Test user registration
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("testuser");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("Test");
        signupRequest.setLastName("User");
        signupRequest.setRole(Set.of("teacher"));

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // Test user login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUserRegistrationWithDuplicateUsername() throws Exception {
        // Create a user first
        createTestUser("existinguser", "existing@example.com");

        // Try to register with same username
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("existinguser");
        signupRequest.setEmail("new@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("New");
        signupRequest.setLastName("User");
        signupRequest.setRole(Set.of("teacher"));

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Username is already taken!"));
    }

    @Test
    void testUserRegistrationWithDuplicateEmail() throws Exception {
        // Create a user first
        createTestUser("user1", "duplicate@example.com");

        // Try to register with same email
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setUsername("user2");
        signupRequest.setEmail("duplicate@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setFirstName("Second");
        signupRequest.setLastName("User");
        signupRequest.setRole(Set.of("teacher"));

        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        // Create a test user
        createTestUser("validuser", "valid@example.com");

        // Try to login with wrong password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("validuser");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testTokenValidation() throws Exception {
        // Create user and get token
        createTestUser("validationuser", "validation@example.com");
        String token = getAuthToken("validationuser", "password123");

        // Test token validation
        mockMvc.perform(get("/api/auth/validate")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token is valid"));
    }

    @Test
    void testGetUserProfile() throws Exception {
        // Create user and get token
        createTestUser("profileuser", "profile@example.com");
        String token = getAuthToken("profileuser", "password123");

        // Test getting user profile
        mockMvc.perform(get("/api/auth/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("profileuser"))
                .andExpect(jsonPath("$.email").value("profile@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"))
                .andExpect(jsonPath("$.roles").isArray());
    }

    @Test
    void testGetAllUsersAsAdmin() throws Exception {
        // Create admin user
        User adminUser = new User();
        adminUser.setUsername("adminuser");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("password123"));
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);
        adminUser.setRoles(roles);
        userRepository.save(adminUser);

        String adminToken = getAuthToken("adminuser", "password123");

        // Create some test users
        createTestUser("user1", "user1@example.com");
        createTestUser("user2", "user2@example.com");

        // Test getting all users as admin
        mockMvc.perform(get("/api/auth/users")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3)))); // admin + 2 users
    }

    private void createTestUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setFirstName("Test");
        user.setLastName("User");
        
        Role teacherRole = roleRepository.findByName("ROLE_TEACHER")
                .orElseThrow(() -> new RuntimeException("Teacher role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(teacherRole);
        user.setRoles(roles);
        
        userRepository.save(user);
    }

    private String getAuthToken(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }
}