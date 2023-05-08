package sysmap.socialmediabackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import sysmap.socialmediabackend.config.jwt.JwtUtils;
import sysmap.socialmediabackend.model.Post;
import sysmap.socialmediabackend.model.Role;
import sysmap.socialmediabackend.model.User;
import sysmap.socialmediabackend.model.postfeatures.Comment;
import sysmap.socialmediabackend.repository.PostRepository;
import sysmap.socialmediabackend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @PostMapping
    public Post createPost(@RequestBody Post post, HttpServletRequest request, Authentication authentication) throws Exception {
        User currentUser = getCurrentUser(request);
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(currentUser);
        post.setAuthor(currentUser.getUsername());
        return postRepository.save(post);
    }

    @GetMapping("/{id}")
    public Post getPostById(@PathVariable String id) {
        return postRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Post updatePost(@PathVariable String id, @RequestBody Post updatedPost) {
        return postRepository.findById(id).map(post -> {
            post.setAuthor(updatedPost.getAuthor());
            post.setContent(updatedPost.getContent());
            return postRepository.save(post);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable String id) {
        postRepository.deleteById(id);
    }

    // Add a comment to a post
    @PostMapping("/{id}/comments")
    public ResponseEntity<Post> addComment(@PathVariable String id, @RequestBody Comment comment, HttpServletRequest request) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            comment.setUser(getCurrentUser(request));
            post.addComment(comment);
            postRepository.save(post);
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Post> removeComment(@PathVariable String id, @PathVariable String commentId, HttpServletRequest request) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if(!optionalPost.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Post post = optionalPost.get();
        User currentUser = getCurrentUser(request);
        post.removeComment(commentId, currentUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> likePost(@PathVariable String id, HttpServletRequest request) {
        User user = getCurrentUser(request);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found. Post id: "+ id));
        if (post.getLikes().contains(user)) {
            return ResponseEntity.badRequest().body("User already liked this post");
        }
        post.addLike(user);
        postRepository.save(post);
        return ResponseEntity.ok("Post liked successfully");
    }

    private User getCurrentUser(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromCookies(request);
        String currentUserName = jwtUtils.getUserNameFromJwtToken(jwt);
        return userRepository.findByUsername(currentUserName).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + currentUserName));
    }
}
