package com.example.bookmyshow.movie.service;

import com.example.bookmyshow.movie.api.dto.MovieCastDTO;
import com.example.bookmyshow.movie.api.dto.person.PersonDTO;
import com.example.bookmyshow.movie.model.Movie;
import com.example.bookmyshow.movie.model.MovieCast;
import com.example.bookmyshow.movie.model.Person;
import com.example.bookmyshow.movie.model.exception.PersonNotFoundException;
import com.example.bookmyshow.movie.repository.MovieCastRepository;
import com.example.bookmyshow.movie.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieCastService {

    private final PersonRepository personRepository;
    private final MovieCastRepository movieCastRepository;

    @Transactional
    public List<MovieCast> resolvePersons(Movie movie, List<MovieCastDTO> names) {

        List<Long> personIds = names.stream()
                .map(MovieCastDTO::getPerson)
                .map(PersonDTO::getPersonId)
                .toList();

        List<Person> existingPersons = personRepository.findByPersonIds(personIds)
                .orElseThrow(() -> new PersonNotFoundException("Person not found, Please check the person Id's"));


        List<MovieCast> response = new ArrayList<>();

        for(Person person : existingPersons){
            MovieCast movieCast = new MovieCast();
            movieCast.setPerson(person);
            movieCast.setMovie(movie);
            response.add(movieCast);
        }

        movieCastRepository.saveAll(response);

        return response;
    }

}




