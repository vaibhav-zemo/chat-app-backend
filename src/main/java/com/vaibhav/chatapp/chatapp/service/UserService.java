package com.vaibhav.chatapp.chatapp.service;

import com.vaibhav.chatapp.chatapp.model.User;
import com.vaibhav.chatapp.chatapp.repository.UserRepository;
import com.vaibhav.chatapp.chatapp.util.constants.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findOrCreateUser(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElseGet(()->
                userRepository.save(
                        User.builder().phoneNumber(phoneNumber).role(Role.USER).build()
                ));
    }

    public User findUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber).orElse(null);
    }
}
