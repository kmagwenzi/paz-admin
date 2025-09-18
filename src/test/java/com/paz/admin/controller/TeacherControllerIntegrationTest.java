package com.paz.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paz.admin.entity.Prison;
import com.paz.admin.entity.Role;
import com.paz.admin.entity.Teacher;
import com.paz.admin.entity.User;
import com.paz.admin.payload.request.LoginRequest;
import com.paz.admin.repository.PrisonRepository;
import com.paz.admin.repository.RoleRepository;
import com.paz.admin.repository.TeacherRepository;
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
class TeacherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TeacherRepository teacherRepository;

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
    private Prison testPrison;

    @BeforeEach
    void setUp() throws Exception {
        teacherRepository.deleteAll();
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

        // Create test prison
        testPrison = new Prison();
        testPrison.setName("Test Prison");
        testPrison.setLocation("Test Location");
        testPrison.setCapacity(100);
        prisonRepository.save(testPrison);

        // Get admin token
        adminToken = getAuthToken("admin", "password123");
    }

    @Test
    void testCreateTeacher() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setEmail("john.doe@example.com");
        teacher.setPhoneNumber("+263771234567");
        teacher.setSpecialization("Mathematics");
        teacher.setYearsOfExperience(5);
        teacher.setPrison(testPrison);

        mockMvc.perform(post("/api/teachers")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacher)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+263771234567"))
                .andExpect(jsonPath("$.specialization").value("Mathematics"))
                .andExpect(jsonPath("$.yearsOfExperience").value(5))
                .andExpect(jsonPath("$.prison.id").value(testPrison.getId()));
    }

    @Test
    void testCreateTeacherWithDuplicateEmail() throws Exception {
        // Create first teacher
        Teacher existingTeacher = new Teacher();
        existingTeacher.setFirstName("Existing");
        existingTeacher.setLastName("Teacher");
        existingTeacher.setEmail("duplicate@example.com");
        existingTeacher.setPhoneNumber("+263771111111");
        existingTeacher.setSpecialization("Science");
        existingTeacher.setYearsOfExperience(3);
        existingTeacher.setPrison(testPrison);
        teacherRepository.save(existingTeacher);

        // Try to create another teacher with same email
        Teacher newTeacher = new Teacher();
        newTeacher.setFirstName("New");
        newTeacher.setLastName("Teacher");
        newTeacher.setEmail("duplicate@example.com");
        newTeacher.setPhoneNumber("+263772222222");
        newTeacher.setSpecialization("Math");
        newTeacher.setYearsOfExperience(2);
        newTeacher.setPrison(testPrison);

        mockMvc.perform(post("/api/teachers")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newTeacher)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email is already in use"));
    }

    @Test
    void testCreateTeacherWithInvalidEmail() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Invalid");
        teacher.setLastName("Email");
        teacher.setEmail("invalid-email");
        teacher.setPhoneNumber("+263771234567");
        teacher.setSpecialization("Math");
        teacher.setYearsOfExperience(2);
        teacher.setPrison(testPrison);

        mockMvc.perform(post("/api/teachers")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacher)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email format"));
    }

    @Test
    void testCreateTeacherWithNonExistentPrison() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("John");
        teacher.setLastName("Doe");
        teacher.setEmail("john@example.com");
        teacher.setPhoneNumber("+263771234567");
        teacher.setSpecialization("Math");
        teacher.setYearsOfExperience(5);

        // Create a prison object with non-existent ID
        Prison nonExistentPrison = new Prison();
        nonExistentPrison.setId(999L);
        teacher.setPrison(nonExistentPrison);

        mockMvc.perform(post("/api/teachers")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(teacher)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Prison not found"));
    }

    @Test
    void testGetAllTeachers() throws Exception {
        // Create some teachers
        Teacher teacher1 = new Teacher();
        teacher1.setFirstName("Teacher");
        teacher1.setLastName("One");
        teacher1.setEmail("one@example.com");
        teacher1.setPhoneNumber("+263771111111");
        teacher1.setSpecialization("Math");
        teacher1.setYearsOfExperience(3);
        teacher1.setPrison(testPrison);
        teacherRepository.save(teacher1);

        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Teacher");
        teacher2.setLastName("Two");
        teacher2.setEmail("two@example.com");
        teacher2.setPhoneNumber("+263772222222");
        teacher2.setSpecialization("Science");
        teacher2.setYearsOfExperience(5);
        teacher2.setPrison(testPrison);
        teacherRepository.save(teacher2);

        mockMvc.perform(get("/api/teachers")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("Teacher"))
                .andExpect(jsonPath("$[1].firstName").value("Teacher"));
    }

    @Test
    void testGetTeacherById() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Specific");
        teacher.setLastName("Teacher");
        teacher.setEmail("specific@example.com");
        teacher.setPhoneNumber("+263773333333");
        teacher.setSpecialization("English");
        teacher.setYearsOfExperience(4);
        teacher.setPrison(testPrison);
        Teacher savedTeacher = teacherRepository.save(teacher);

        mockMvc.perform(get("/api/teachers/" + savedTeacher.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Specific"))
                .andExpect(jsonPath("$.lastName").value("Teacher"))
                .andExpect(jsonPath("$.email").value("specific@example.com"))
                .andExpect(jsonPath("$.specialization").value("English"))
                .andExpect(jsonPath("$.yearsOfExperience").value(4));
    }

    @Test
    void testGetNonExistentTeacher() throws Exception {
        mockMvc.perform(get("/api/teachers/999")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTeacher() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("Old");
        teacher.setLastName("Name");
        teacher.setEmail("old@example.com");
        teacher.setPhoneNumber("+263774444444");
        teacher.setSpecialization("History");
        teacher.setYearsOfExperience(2);
        teacher.setPrison(testPrison);
        Teacher savedTeacher = teacherRepository.save(teacher);

        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setFirstName("New");
        updatedTeacher.setLastName("Name");
        updatedTeacher.setEmail("new@example.com");
        updatedTeacher.setPhoneNumber("+263775555555");
        updatedTeacher.setSpecialization("Geography");
        updatedTeacher.setYearsOfExperience(6);
        updatedTeacher.setPrison(testPrison);

        mockMvc.perform(put("/api/teachers/" + savedTeacher.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedTeacher)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("New"))
                .andExpect(jsonPath("$.lastName").value("Name"))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("+263775555555"))
                .andExpect(jsonPath("$.specialization").value("Geography"))
                .andExpect(jsonPath("$.yearsOfExperience").value(6));
    }

    @Test
    void testDeleteTeacher() throws Exception {
        Teacher teacher = new Teacher();
        teacher.setFirstName("To");
        teacher.setLastName("Delete");
        teacher.setEmail("delete@example.com");
        teacher.setPhoneNumber("+263776666666");
        teacher.setSpecialization("Art");
        teacher.setYearsOfExperience(1);
        teacher.setPrison(testPrison);
        Teacher savedTeacher = teacherRepository.save(teacher);

        mockMvc.perform(delete("/api/teachers/" + savedTeacher.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Teacher deleted successfully"));

        // Verify teacher is deleted
        mockMvc.perform(get("/api/teachers/" + savedTeacher.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTeachersByPrison() throws Exception {
        // Create another prison
        Prison otherPrison = new Prison();
        otherPrison.setName("Other Prison");
        otherPrison.setLocation("Other Location");
        otherPrison.setCapacity(200);
        prisonRepository.save(otherPrison);

        // Create teachers for different prisons
        Teacher teacher1 = new Teacher();
        teacher1.setFirstName("Prison");
        teacher1.setLastName("One");
        teacher1.setEmail("prison1@example.com");
        teacher1.setPhoneNumber("+263777777777");
        teacher1.setSpecialization("Math");
        teacher1.setYearsOfExperience(3);
        teacher1.setPrison(testPrison);
        teacherRepository.save(teacher1);

        Teacher teacher2 = new Teacher();
        teacher2.setFirstName("Prison");
        teacher2.setLastName("Two");
        teacher2.setEmail("prison2@example.com");
        teacher2.setPhoneNumber("+263778888888");
        teacher2.setSpecialization("Science");
        teacher2.setYearsOfExperience(4);
        teacher2.setPrison(otherPrison);
        teacherRepository.save(teacher2);

        // Get teachers for test prison
        mockMvc.perform(get("/api/teachers/prison/" + testPrison.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("prison1@example.com"));
    }

    @Test
    void testGetTeachersBySpecialization() throws Exception {
        // Create teachers with different specializations
        Teacher mathTeacher = new Teacher();
        mathTeacher.setFirstName("Math");
        mathTeacher.setLastName("Teacher");
        mathTeacher.setEmail("math@example.com");
        mathTeacher.setPhoneNumber("+263779999999");
        mathTeacher.setSpecialization("Mathematics");
        mathTeacher.setYearsOfExperience(5);
        mathTeacher.setPrison(testPrison);
        teacherRepository.save(mathTeacher);

        Teacher scienceTeacher = new Teacher();
        scienceTeacher.setFirstName("Science");
        scienceTeacher.setLastName("Teacher");
        scienceTeacher.setEmail("science@example.com");
        scienceTeacher.setPhoneNumber("+263770000000");
        scienceTeacher.setSpecialization("Science");
        scienceTeacher.setYearsOfExperience(3);
        scienceTeacher.setPrison(testPrison);
        teacherRepository.save(scienceTeacher);

        // Get math teachers
        mockMvc.perform(get("/api/teachers/specialization/Mathematics")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("math@example.com"));
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