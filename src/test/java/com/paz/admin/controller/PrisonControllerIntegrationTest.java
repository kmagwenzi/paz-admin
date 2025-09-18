package com.paz.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paz.admin.entity.Prison;
import com.paz.admin.entity.Role;
import com.paz.admin.entity.User;
import com.paz.admin.payload.request.LoginRequest;
import com.paz.admin.repository.PrisonRepository;
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
class PrisonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrisonRepository prisonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String managerToken;

    @BeforeEach
    void setUp() throws Exception {
        prisonRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        // Create roles
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        roleRepository.save(adminRole);

        Role managerRole = new Role();
        managerRole.setName("ROLE_PRISON_MANAGER");
        roleRepository.save(managerRole);

        Role teacherRole = new Role();
        teacherRole.setName("ROLE_TEACHER");
        roleRepository.save(teacherRole);

        // Create admin user
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("password123"));
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        
        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);
        adminUser.setRoles(adminRoles);
        userRepository.save(adminUser);

        // Create prison manager user
        User managerUser = new User();
        managerUser.setUsername("manager");
        managerUser.setEmail("manager@example.com");
        managerUser.setPassword(passwordEncoder.encode("password123"));
        managerUser.setFirstName("Manager");
        managerUser.setLastName("User");
        
        Set<Role> managerRoles = new HashSet<>();
        managerRoles.add(managerRole);
        managerUser.setRoles(managerRoles);
        userRepository.save(managerUser);

        // Get tokens
        adminToken = getAuthToken("admin", "password123");
        managerToken = getAuthToken("manager", "password123");
    }

    @Test
    void testCreatePrisonAsAdmin() throws Exception {
        Prison prison = new Prison();
        prison.setName("Harare Central Prison");
        prison.setLocation("Harare CBD");
        prison.setCapacity(500);
        prison.setCurrentPopulation(250);
        prison.setContactEmail("harare@prison.gov.zw");
        prison.setContactPhone("+263242123456");

        mockMvc.perform(post("/api/prisons")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prison)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Harare Central Prison"))
                .andExpect(jsonPath("$.location").value("Harare CBD"))
                .andExpect(jsonPath("$.capacity").value(500))
                .andExpect(jsonPath("$.currentPopulation").value(250))
                .andExpect(jsonPath("$.contactEmail").value("harare@prison.gov.zw"))
                .andExpect(jsonPath("$.contactPhone").value("+263242123456"));
    }

    @Test
    void testCreatePrisonAsManager() throws Exception {
        Prison prison = new Prison();
        prison.setName("Manager Prison");
        prison.setLocation("Manager Location");
        prison.setCapacity(300);

        mockMvc.perform(post("/api/prisons")
                .header("Authorization", "Bearer " + managerToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prison)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreatePrisonWithDuplicateName() throws Exception {
        // Create first prison
        Prison existingPrison = new Prison();
        existingPrison.setName("Duplicate Prison");
        existingPrison.setLocation("Location 1");
        existingPrison.setCapacity(100);
        prisonRepository.save(existingPrison);

        // Try to create another prison with same name
        Prison newPrison = new Prison();
        newPrison.setName("Duplicate Prison");
        newPrison.setLocation("Location 2");
        newPrison.setCapacity(200);

        mockMvc.perform(post("/api/prisons")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPrison)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Prison name is already taken"));
    }

    @Test
    void testGetAllPrisons() throws Exception {
        // Create some prisons
        Prison prison1 = new Prison();
        prison1.setName("Prison 1");
        prison1.setLocation("Location 1");
        prison1.setCapacity(100);
        prisonRepository.save(prison1);

        Prison prison2 = new Prison();
        prison2.setName("Prison 2");
        prison2.setLocation("Location 2");
        prison2.setCapacity(200);
        prisonRepository.save(prison2);

        mockMvc.perform(get("/api/prisons")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Prison 1"))
                .andExpect(jsonPath("$[1].name").value("Prison 2"));
    }

    @Test
    void testGetPrisonById() throws Exception {
        Prison prison = new Prison();
        prison.setName("Test Prison");
        prison.setLocation("Test Location");
        prison.setCapacity(150);
        Prison savedPrison = prisonRepository.save(prison);

        mockMvc.perform(get("/api/prisons/" + savedPrison.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Prison"))
                .andExpect(jsonPath("$.location").value("Test Location"))
                .andExpect(jsonPath("$.capacity").value(150));
    }

    @Test
    void testGetNonExistentPrison() throws Exception {
        mockMvc.perform(get("/api/prisons/999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePrison() throws Exception {
        Prison prison = new Prison();
        prison.setName("Old Name");
        prison.setLocation("Old Location");
        prison.setCapacity(100);
        Prison savedPrison = prisonRepository.save(prison);

        Prison updatedPrison = new Prison();
        updatedPrison.setName("New Name");
        updatedPrison.setLocation("New Location");
        updatedPrison.setCapacity(200);
        updatedPrison.setContactEmail("new@prison.gov.zw");
        updatedPrison.setContactPhone("+263242654321");

        mockMvc.perform(put("/api/prisons/" + savedPrison.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPrison)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.location").value("New Location"))
                .andExpect(jsonPath("$.capacity").value(200))
                .andExpect(jsonPath("$.contactEmail").value("new@prison.gov.zw"))
                .andExpect(jsonPath("$.contactPhone").value("+263242654321"));
    }

    @Test
    void testUpdatePrisonWithDuplicateName() throws Exception {
        // Create two prisons
        Prison prison1 = new Prison();
        prison1.setName("Prison A");
        prison1.setLocation("Location A");
        prison1.setCapacity(100);
        prisonRepository.save(prison1);

        Prison prison2 = new Prison();
        prison2.setName("Prison B");
        prison2.setLocation("Location B");
        prison2.setCapacity(200);
        Prison savedPrison2 = prisonRepository.save(prison2);

        // Try to update prison2 with prison1's name
        Prison updatedPrison = new Prison();
        updatedPrison.setName("Prison A");
        updatedPrison.setLocation("Location B");
        updatedPrison.setCapacity(200);

        mockMvc.perform(put("/api/prisons/" + savedPrison2.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedPrison)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Prison name is already taken"));
    }

    @Test
    void testDeletePrison() throws Exception {
        Prison prison = new Prison();
        prison.setName("To Delete");
        prison.setLocation("Delete Location");
        prison.setCapacity(50);
        Prison savedPrison = prisonRepository.save(prison);

        mockMvc.perform(delete("/api/prisons/" + savedPrison.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Prison deleted successfully"));

        // Verify prison is deleted
        mockMvc.perform(get("/api/prisons/" + savedPrison.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteNonExistentPrison() throws Exception {
        mockMvc.perform(delete("/api/prisons/999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Prison not found with id: 999"));
    }

    @Test
    void testCreatePrisonWithInvalidEmail() throws Exception {
        Prison prison = new Prison();
        prison.setName("Invalid Email Prison");
        prison.setLocation("Test Location");
        prison.setCapacity(100);
        prison.setContactEmail("invalid-email");

        mockMvc.perform(post("/api/prisons")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prison)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid contact email format"));
    }

    @Test
    void testCreatePrisonWithNegativeCapacity() throws Exception {
        Prison prison = new Prison();
        prison.setName("Negative Capacity");
        prison.setLocation("Test Location");
        prison.setCapacity(-1);

        mockMvc.perform(post("/api/prisons")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prison)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Capacity must be greater than 0"));
    }

    @Test
    void testCreatePrisonWithNegativePopulation() throws Exception {
        Prison prison = new Prison();
        prison.setName("Negative Population");
        prison.setLocation("Test Location");
        prison.setCapacity(100);
        prison.setCurrentPopulation(-5);

        mockMvc.perform(post("/api/prisons")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prison)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Current population cannot be negative"));
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