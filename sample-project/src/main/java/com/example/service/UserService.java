package com.example.service;

public interface UserService {
    void createUser(String username, String email);
    void deleteUser(String username);
    boolean userExists(String username);
} 