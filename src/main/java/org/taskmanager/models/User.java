package org.taskmanager.models;

import java.util.Objects;

public class User {
    private int user_id;
    private  String username;
    private String password;
    private String role;

    public User(int user_id, String username, String password, String role) {

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("role не может быть пустым");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Хэш пароля не может быть пустым");
        }

        this.user_id = user_id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String username, String password, String role) {
        this(0, username, password, role);
    }

    public boolean isCorrectForCreate(User user){
         return user.username != null && !user.username.trim().isEmpty() &&
                 user.password != null && !user.password.trim().isEmpty() &&
                 user.role != null && !user.role.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return user_id == user.user_id && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, username);
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
