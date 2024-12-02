package com.krish.SpringJWTAuthApp.repo;

import com.krish.SpringJWTAuthApp.models.ERole;
import com.krish.SpringJWTAuthApp.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
