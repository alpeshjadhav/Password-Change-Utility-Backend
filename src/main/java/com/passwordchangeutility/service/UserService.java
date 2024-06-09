package com.passwordchangeutility.service;

import com.passwordchangeutility.model.User;

import java.nio.file.attribute.UserPrincipalNotFoundException;

public interface UserService {

    User createUser(User user);
    User getUserById(String id);
    User getUserByUsername(String username);
    void updatePassword(String id, String newPassword) throws UserPrincipalNotFoundException;
}
