package sysmap.socialmediabackend.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import sysmap.socialmediabackend.model.postfeatures.Comment;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Embedded;

import org.springframework.data.mongodb.core.mapping.DBRef;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "posts")
public class Post {
    @Id
    private String id;
    private String author;
    private String content;
    @CreatedDate
    private LocalDateTime createdAt;

    @DBRef
    private User user;

    @Embedded
    private List<Comment> comments = new ArrayList<>();
    
    @DBRef
    private Set<User> likes = new HashSet<>();

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(String commentId, User currentUser) throws RuntimeException {
        Optional<Comment> commentOptional = this.comments.stream()
            .filter(comment -> comment.getId().equals(commentId))
            .findFirst();
        
        if (commentOptional.isPresent()) {
            this.comments.remove(commentOptional.get());
        } else {
            throw new RuntimeException("Comment not found");        
        }
    }

    public void addLike(User user) {
        this.likes.add(user);
    }

    public void removeLike(String userId) {
        this.likes.removeIf(user -> user.getId().equals(userId));
    }

    public Set<String> likeIdSet() {
        return this.likes.stream().map(User::getId).collect(Collectors.toSet());
    }

    public User getCommentOwner(String commentId) throws RuntimeException {
        Optional<Comment> commentOptional = this.comments.stream()
        .filter(comment -> comment.getId().equals(commentId))
        .findFirst();
        if (commentOptional.isPresent()){
            return commentOptional.get().getUser();
        } else {
            throw new RuntimeException("Comment not found");
        }
    }
}
