package sysmap.socialmediabackend.controller;

import sysmap.socialmediabackend.model.User;
import sysmap.socialmediabackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCrypt;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        
        // hash user pass before saving
        String hashedPassword = bCrypt.encode(user.getPassword());
        user.setPassword(hashedPassword);
        
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

}
