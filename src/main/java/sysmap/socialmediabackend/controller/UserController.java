package sysmap.socialmediabackend.controller;

import sysmap.socialmediabackend.config.jwt.JwtUtils;
import sysmap.socialmediabackend.model.User;
import sysmap.socialmediabackend.repository.UserRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
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
    
    @PostMapping("/{userid}/follow")
    public ResponseEntity<String> followUser(@PathVariable String userId, HttpServletRequest request) {
        Optional<User> optionalProfileOwner = userRepository.findByUsername(userId);
        if(!optionalProfileOwner.isPresent()){
            return ResponseEntity.badRequest().body("User with ID: "+userId+" does not exist");
        }
        User profileOwner = optionalProfileOwner.get();
        String jwt = jwtUtils.getJwtFromCookies(request);
        String currentUserName = jwtUtils.getUserNameFromJwtToken(jwt);
        User currentUser = userRepository.findByUsername(currentUserName).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + currentUserName));
        if(currentUser.followingIdSet().contains(userId)){
            currentUser.removeFollowing(profileOwner.getId());
            userRepository.save(currentUser);
            profileOwner.removeFollower(currentUser.getId());
            userRepository.save(profileOwner);
            return ResponseEntity.ok("Unfollowed "+profileOwner.getUsername()+" successfully!");
        }
        currentUser.setFollowing(profileOwner);
        userRepository.save(currentUser);
        profileOwner.setFollowers(currentUser);
        userRepository.save(profileOwner);
        return ResponseEntity.ok("Followed "+profileOwner.getUsername()+" successfully!");



    }
}
