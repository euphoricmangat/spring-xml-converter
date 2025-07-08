package com.example.service;

import com.example.dao.UserDao;
import com.example.service.EmailService;

public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private EmailService emailService;
    private int maxUsers;
    
    public UserServiceImpl() {
    }
    
    public UserServiceImpl(UserDao userDao, EmailService emailService) {
        this.userDao = userDao;
        this.emailService = emailService;
    }
    
    @Override
    public void createUser(String username, String email) {
        userDao.saveUser(username, email);
        emailService.sendWelcomeEmail(email);
    }
    
    @Override
    public void deleteUser(String username) {
        userDao.deleteUser(username);
    }
    
    @Override
    public boolean userExists(String username) {
        return userDao.findUser(username) != null;
    }
    
    // Getters and setters for XML property injection
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
    
    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }
    
    public UserDao getUserDao() {
        return userDao;
    }
    
    public EmailService getEmailService() {
        return emailService;
    }
    
    public int getMaxUsers() {
        return maxUsers;
    }
} 