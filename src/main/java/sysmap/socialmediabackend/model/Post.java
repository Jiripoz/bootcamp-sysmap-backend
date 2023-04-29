package sysmap.socialmediabackend.model;

import java.util.UUID;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "posts")
public class Post {
    @Id
    private String id = UUID.randomUUID().toString();;
    private String author;
    private String content;
    private LocalDateTime createdAt;

    @DBRef
    private User user;
}
