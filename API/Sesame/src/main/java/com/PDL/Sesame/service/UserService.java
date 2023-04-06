package com.PDL.Sesame.service;

import com.PDL.Sesame.model.*;

import java.util.List;



public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long id);

    User updateUser(Long id, User user);

    void deleteUser(Long id);




}
