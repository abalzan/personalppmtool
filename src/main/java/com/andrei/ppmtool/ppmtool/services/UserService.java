package com.andrei.ppmtool.ppmtool.services;

import com.andrei.ppmtool.ppmtool.Repositories.UserRepository;
import com.andrei.ppmtool.ppmtool.exceptions.UsernameAlreadyExistException;
import com.andrei.ppmtool.ppmtool.model.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public User saveUser(User newUser) {
        try {
            newUser.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
            newUser.setUsername(newUser.getUsername());

            return userRepository.save(newUser);
        } catch (Exception e) {
            log.error(e);
            throw new UsernameAlreadyExistException("User with name " + newUser.getUsername() + "already exists!!!");
        }
    }
}
