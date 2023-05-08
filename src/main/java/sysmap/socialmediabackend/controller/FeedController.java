package sysmap.socialmediabackend.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;
import sysmap.socialmediabackend.config.jwt.JwtUtils;
import sysmap.socialmediabackend.model.Post;
import sysmap.socialmediabackend.model.User;
import sysmap.socialmediabackend.repository.PostRepository;
import sysmap.socialmediabackend.repository.UserRepository;
import sysmap.socialmediabackend.services.AuthorizationService;

@RestController
@RequestMapping("/api")
public class FeedController {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping("/foryou")
    public ResponseEntity<List<Post>> forYou(HttpServletRequest request){
        User currentUser = getCurrentUser(request);
        if (!authorizationService.isAuthorized("can_see_posts", currentUser.getRoles())){
            return ResponseEntity.notFound().build();
        }
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        List<String> followingIdList = new ArrayList<>(currentUser.followerIdSet());
        List<Post> posts = postRepository.findByUserIdInOrderByCreatedAt(followingIdList, pageable); 
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/home")
    public ResponseEntity<List<Post>> homePage(HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        if (!authorizationService.isAuthorized("can_see_posts", currentUser.getRoles())){
            return ResponseEntity.notFound().build();
        }
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        List<Post> posts = postRepository.findByUserId(currentUser.getId(), pageable); 
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Post>> userProfile(@PathVariable String userId, HttpServletRequest request){
        User currentUser = getCurrentUser(request);
        if (!authorizationService.isAuthorized("can_see_posts", currentUser.getRoles())){
            return ResponseEntity.notFound().build();
        }
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());
        List<Post> posts = postRepository.findByUserId(userId, pageable); 
        return ResponseEntity.ok(posts);
    }

    private User getCurrentUser(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromCookies(request);
        String currentUserName = jwtUtils.getUserNameFromJwtToken(jwt);
        return userRepository.findByUsername(currentUserName).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + currentUserName));
    }

    
}
