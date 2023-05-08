package sysmap.socialmediabackend.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import sysmap.socialmediabackend.model.postfeatures.Comment;
import sysmap.socialmediabackend.model.postfeatures.Like;

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
    
    @Embedded
    private List<Like> likes;

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void addLike(Like like) {
        this.likes.add(like);
    }
}
