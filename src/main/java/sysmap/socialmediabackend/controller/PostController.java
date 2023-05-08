package sysmap.socialmediabackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import sysmap.socialmediabackend.model.Post;
import sysmap.socialmediabackend.model.User;
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
        System.out.println(userRepository.findAll());
        String currentUserName = authentication.getName();
        System.out.println("The current username is: "+currentUserName);
        User currentUser = userRepository.findByUsername(currentUserName)
                                         .orElseThrow(() -> new Exception("User not found"));
        post.setUser(currentUser);
        post.setAuthor(currentUserName);
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
}
