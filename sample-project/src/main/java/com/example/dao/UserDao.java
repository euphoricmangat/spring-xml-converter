package com.example.dao;

public interface UserDao {
    void saveUser(String username, String email);
    void deleteUser(String username);
    String findUser(String username);
} 