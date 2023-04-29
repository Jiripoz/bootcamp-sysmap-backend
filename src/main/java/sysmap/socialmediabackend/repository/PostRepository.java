package sysmap.socialmediabackend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import sysmap.socialmediabackend.model.Post;

public interface PostRepository extends MongoRepository<Post, String> {
}
