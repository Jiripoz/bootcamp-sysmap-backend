package sysmap.socialmediabackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import sysmap.socialmediabackend.model.Post;
import sysmap.socialmediabackend.model.User;
import sysmap.socialmediabackend.model.postfeatures.Comment;
import sysmap.socialmediabackend.model.postfeatures.Like;
import sysmap.socialmediabackend.repository.PostRepository;
import sysmap.socialmediabackend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

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
        post.setCreatedAt(LocalDateTime.now());
        User user = getCurrentUser();
        post.setUser(user);
        post.setAuthor(user.getUsername());
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
    public ResponseEntity<Post> addComment(@PathVariable String id, @RequestBody Comment comment) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            comment.setUser(getCurrentUser());
            post.addComment(comment);
            postRepository.save(post);
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<Post> addLike(@PathVariable String id, @RequestBody Like like) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            User currentUser = getCurrentUser();
            if (!post.getLikes().contains(like)) {
                like.setUser(currentUser);
                post.addLike(like);
                postRepository.save(post);
            }
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        return userRepository.findByUsername(currentUserName).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + currentUserName));
    }
}
