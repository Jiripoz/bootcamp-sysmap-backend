package sysmap.socialmediabackend.model.postfeatures;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sysmap.socialmediabackend.model.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    private String id = UUID.randomUUID().toString();
    private String content;
    @CreatedDate
    private LocalDateTime createdAt;

    @DBRef
    private User user;
}
