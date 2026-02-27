package com.example.bookmyshow.movie.repository;

import com.example.bookmyshow.movie.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByPersonId(Long personId);

    @Query("SELECT p FROM Person p WHERE p.personId IN :personIds")
    Optional<List<Person>> findByPersonIds(@Param("personIds") List<Long> personIds);
}




