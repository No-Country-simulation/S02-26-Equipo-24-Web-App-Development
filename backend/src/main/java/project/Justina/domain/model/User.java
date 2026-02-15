package project.Justina.domain.model;

import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String password;
    private String role;

    public User(UUID id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
