package sysmap.socialmediabackend.controller;

import sysmap.socialmediabackend.config.jwt.JwtUtils;
import sysmap.socialmediabackend.model.User;
import sysmap.socialmediabackend.repository.UserRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;
    
    @GetMapping
    public void teste(HttpServletRequest request) {
        User user = getCurrentUser(request);
        System.out.println("teste teste teste steste"+user.getId());
    }

    @GetMapping("/{id}/follow")
    public ResponseEntity<String> followUser(@PathVariable String id, HttpServletRequest request) {
        Optional<User> optionalProfileOwner = userRepository.findById(id);
        if(!optionalProfileOwner.isPresent()){
            return ResponseEntity.badRequest().body("User with ID: "+id+" does not exist");
        }
        User profileOwner = optionalProfileOwner.get();
        String jwt = jwtUtils.getJwtFromCookies(request);
        String currentUserName = jwtUtils.getUserNameFromJwtToken(jwt);
        User currentUser = userRepository.findByUsername(currentUserName).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + currentUserName));
        if(currentUser.followingIdSet().contains(id)){
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

    private User getCurrentUser(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromCookies(request);
        String currentUserName = jwtUtils.getUserNameFromJwtToken(jwt);
        return userRepository.findByUsername(currentUserName).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + currentUserName));
    }
}
