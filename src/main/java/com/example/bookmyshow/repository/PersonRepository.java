package com.example.bookmyshow.repository;

import com.example.bookmyshow.models.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends CrudRepository<Person, Long> {
    Optional<Person> findByPersonId(long personId);
}
