package doxabeta;

/**
 * Represents a mentor who can be assigned to students.
 * Kept deliberately minimal for the incomplete prototype.
 */
public class Mentor {

    private final String name;
    private final String email;
    private final String role;

    public Mentor(String name, String email, String role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return String.format("%-20s %-30s %s", name, email, role);
    }
}
