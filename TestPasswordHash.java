import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestPasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String hashedPassword = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("Hashed: " + hashedPassword);
        System.out.println("Matches: " + encoder.matches(password, hashedPassword));
        
        // Test with the placeholder hash from the SQL file
        String placeholderHash = "$2a$10$r6Q8b8q8q8q8q8q8q8q8u8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q8q";
        System.out.println("Placeholder matches: " + encoder.matches(password, placeholderHash));
    }
}