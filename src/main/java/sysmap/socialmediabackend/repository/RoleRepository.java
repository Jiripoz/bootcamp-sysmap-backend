package sysmap.socialmediabackend.repository;

import sysmap.socialmediabackend.model.Role;
import sysmap.socialmediabackend.model.ERole;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}