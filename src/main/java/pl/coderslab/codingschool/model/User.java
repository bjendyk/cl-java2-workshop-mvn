package pl.coderslab.codingschool.model;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {
    private long id;
    private String username;
    private String password;
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private User() {}

    public User(String username, String password, String email) {
        this.username = username;
        this.email = email;
        setPassword(password);
    }

    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void saveToDB(Connection conn) throws SQLException {
        if (this.id == 0) {
            String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, this.username);
            stmt.setString(2, this.password);
            stmt.setString(3, this.email);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        } else {
            String sql = "UPDATE users SET username=?, password=?, email=? WHERE id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, this.username);
            stmt.setString(2, this.password);
            stmt.setString(3, this.email);
            stmt.setLong(4, this.id);
            stmt.executeUpdate();
        }
    }

    public void delete(Connection conn) throws SQLException {
        if (this.id == 0) {
            return;
        }

        String sql = "DELETE FROM users WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setLong(1, this.id);
        stmt.executeUpdate();
        this.id = 0;
    }

    public static User getUserById(Connection conn, long id) throws SQLException {
        String sql = "SELECT id, username, email, password FROM users WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setLong(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            User user = new User();
            user.id = rs.getLong("id");
            user.username = rs.getString("username");
            user.email = rs.getString("email");
            user.password = rs.getString("password");
            return user;
        }

        return null;
    }

    public static ArrayList<User> getAllUsers(Connection conn) throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT id, username, email, password FROM users";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            User user = new User();
            user.id = rs.getLong("id");
            user.username = rs.getString("username");
            user.email = rs.getString("email");
            user.password = rs.getString("password");
            users.add(user);
        }

        return users;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Hi, this is an instance of User class.\n");
        sb.append(String.format("* ID: %d\n", this.id));
        sb.append(String.format("* username: %s\n", this.username));
        sb.append(String.format("* email: %s\n", this.email));
        return sb.toString();
    }
}
