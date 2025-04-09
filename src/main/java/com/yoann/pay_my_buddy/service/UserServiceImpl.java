package com.yoann.pay_my_buddy.service;

import com.yoann.pay_my_buddy.model.User;
import com.yoann.pay_my_buddy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public boolean removeUser(User toDeleteUser) {
        return true;
    }
}
