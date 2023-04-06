package com.PDL.Sesame.service;

import com.PDL.Sesame.Exception.ResourceNotFoundException;
import com.PDL.Sesame.dao.UserDao;
import com.PDL.Sesame.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }


    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userDao.findById(id);
        if(user.isPresent()) {
            return user.get();
        }
        else {
            throw new ResourceNotFoundException("User", "id", id);
        }
    }

    @Override
    public User updateUser(Long id, User user) {
        User existingUser = userDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        existingUser.setEmail(user.getEmail());
        existingUser.setFirstname(user.getFirstname());
        existingUser.setLastname(user.getLastname());
        return userDao.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userDao.delete(user);
    }

}
