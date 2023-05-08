package sysmap.socialmediabackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import sysmap.socialmediabackend.config.jwt.JwtUtils;
import sysmap.socialmediabackend.model.Post;
import sysmap.socialmediabackend.model.User;
import sysmap.socialmediabackend.model.postfeatures.Comment;
import sysmap.socialmediabackend.repository.PostRepository;
import sysmap.socialmediabackend.repository.UserRepository;
import sysmap.socialmediabackend.services.AuthorizationService;

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
    @Autowired
    private AuthorizationService authorizationService;

    @GetMapping
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post, HttpServletRequest request, Authentication authentication) throws Exception {
        User currentUser = getCurrentUser(request);
        if (!authorizationService.isAuthorized("can_create_posts", currentUser.getRoles())){
            return ResponseEntity.notFound().build();
        }
        post.setCreatedAt(LocalDateTime.now());
        post.setUser(currentUser);
        post.setAuthor(currentUser.getUsername());
        postRepository.save(post);
        return ResponseEntity.ok(post);
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

    @PostMapping("/{id}/comments")
    public ResponseEntity<Post> addComment(@PathVariable String id, @RequestBody Comment comment, HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        if (!authorizationService.isAuthorized("can_comment_on_posts", currentUser.getRoles())){
            return ResponseEntity.notFound().build();
        }
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            comment.setUser(getCurrentUser(request));
            comment.setCreatedAt(LocalDateTime.now());
            post.addComment(comment);
            postRepository.save(post);
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<Post> removeComment(@PathVariable String id, @PathVariable String commentId, HttpServletRequest request) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if(!optionalPost.isPresent()){
            return ResponseEntity.notFound().build();
        }
        Post post = optionalPost.get();

        User currentUser = getCurrentUser(request);
        User commentOwner = post.getCommentOwner(commentId);

        if (!(authorizationService.isAuthorized("can_delete_comments", currentUser.getRoles()) || (commentOwner.equals(currentUser))
        )){
            return ResponseEntity.badRequest().build();
        }
        post.removeComment(commentId, currentUser);
        postRepository.save(post);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> likePost(@PathVariable String id, HttpServletRequest request) {
        User currentUser = getCurrentUser(request);
        if (!authorizationService.isAuthorized("can_like_posts", currentUser.getRoles())){
            return ResponseEntity.badRequest().body("User lack authentication");
        }
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found. Post id: "+ id));
        Set<String> likeByUserId = post.likeIdSet();
        if (likeByUserId.contains(currentUser.getId())) {
            post.removeLike(currentUser.getId());
            postRepository.save(post);
            return ResponseEntity.ok("Post unliked successfully");
        }
        post.addLike(currentUser);
        postRepository.save(post);
        return ResponseEntity.ok("Post liked successfully");
    }

    private User getCurrentUser(HttpServletRequest request) {
        String jwt = jwtUtils.getJwtFromCookies(request);
        String currentUserName = jwtUtils.getUserNameFromJwtToken(jwt);
        return userRepository.findByUsername(currentUserName).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + currentUserName));
    }
}
