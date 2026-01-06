package com.example.bookmyshow.repository;

import com.example.bookmyshow.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findByKeyCloakId(String keyCloakId);
}
