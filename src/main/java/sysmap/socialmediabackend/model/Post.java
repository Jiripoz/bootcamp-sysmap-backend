package sysmap.socialmediabackend.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private List<Comment> comments;
    
    @DBRef
    private Set<User> likes;

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(String commentId, User currentUser) {
        if (currentUser.getId().equals(this.user.getId()) ||
            currentUser.getRoles().stream().anyMatch(role -> role.getName() == ERole.ROLE_ADMIN) || 
            currentUser.getRoles().stream().anyMatch(role -> role.getName() == ERole.ROLE_MODERATOR)) {
            
            Optional<Comment> commentOptional = this.comments.stream()
                .filter(comment -> comment.getId().equals(commentId))
                .findFirst();
            
            if (commentOptional.isPresent()) {
                this.comments.remove(commentOptional.get());
            } else {
                throw new RuntimeException("Comment not found");        
            }
        } else {
        throw new RuntimeException("User does not have the necessary privileges to remove this comment");
        }
    }
    
    public void addLike(User user) {
        this.likes.add(user);
    }

    public void removeLike(User user) {
        this.likes.remove(user);
    }
}
