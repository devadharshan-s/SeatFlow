package org.example.bookmyshowmovieservice.movie.service;

import org.example.bookmyshowmovieservice.movie.api.dto.MovieCastDTO;
import org.example.bookmyshowmovieservice.movie.api.dto.person.PersonDTO;
import org.example.bookmyshowmovieservice.movie.model.Movie;
import org.example.bookmyshowmovieservice.movie.model.MovieCast;
import org.example.bookmyshowmovieservice.movie.model.Person;
import org.example.bookmyshowmovieservice.movie.model.exception.PersonNotFoundException;
import org.example.bookmyshowmovieservice.movie.repository.MovieCastRepository;
import org.example.bookmyshowmovieservice.movie.repository.PersonRepository;
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





