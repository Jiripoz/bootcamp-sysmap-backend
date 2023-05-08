package sysmap.socialmediabackend.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import sysmap.socialmediabackend.model.Post;


@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    @Query("{ '_id' : ?0 }")
    Optional <Post> findById(String id);

    @Query("{ 'user.$id' : ?0 }")
    List<Post> findByUserId(String userId, Pageable pageable);
    
    List<Post> findByUserIdInOrderByCreatedAt(List<String> userIds, Pageable pageable);
    
}
