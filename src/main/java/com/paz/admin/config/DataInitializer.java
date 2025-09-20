package com.paz.admin.config;

import com.paz.admin.entity.Role;
import com.paz.admin.entity.User;
import com.paz.admin.repository.RoleRepository;
import com.paz.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@Profile("test")
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Create roles if they don't exist
        Role adminRole = createRoleIfNotExists("ROLE_ADMIN", "Administrator");
        Role managerRole = createRoleIfNotExists("ROLE_PRISON_MANAGER", "Prison Manager");
        Role teacherRole = createRoleIfNotExists("ROLE_TEACHER", "Teacher");

        // Create test users if they don't exist
        createUserIfNotExists("testuser", "test@example.com", "password123", "Test", "User", teacherRole);
        createUserIfNotExists("admin", "admin@paz.org.zw", "password123", "System", "Administrator", adminRole);
        
        System.out.println("DEBUG: Test data initialized successfully");
    }

    private Role createRoleIfNotExists(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(name);
                    role.setDescription(description);
                    return roleRepository.save(role);
                });
    }

    private void createUserIfNotExists(String username, String email, String password, 
                                      String firstName, String lastName, Role role) {
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password); // Plaintext password since we're using NoOpPasswordEncoder
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEnabled(true);
            
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            
            userRepository.save(user);
            System.out.println("DEBUG: Created user: " + username);
        }
    }
}