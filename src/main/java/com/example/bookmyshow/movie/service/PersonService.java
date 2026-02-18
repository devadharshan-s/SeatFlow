package com.example.bookmyshow.movie.service;

import com.example.bookmyshow.exception.MovieNotFoundException;
import com.example.bookmyshow.movie.api.dto.person.PersonCreateDTO;
import com.example.bookmyshow.movie.api.dto.person.PersonMoviesResponseDTO;
import com.example.bookmyshow.movie.api.dto.person.PersonMoviesUpdateDTO;
import com.example.bookmyshow.movie.api.dto.person.PersonResponseDTO;
import com.example.bookmyshow.movie.api.dto.person.PersonUpdateDTO;
import com.example.bookmyshow.movie.model.Movie;
import com.example.bookmyshow.movie.model.MovieCast;
import com.example.bookmyshow.movie.model.Person;
import com.example.bookmyshow.movie.model.exception.PersonNotFoundException;
import com.example.bookmyshow.movie.repository.MovieRepository;
import com.example.bookmyshow.movie.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;
    private final ModelMapper modelMapper;
    private final MovieRepository movieRepository;

    @Transactional
    public PersonResponseDTO savePerson(PersonCreateDTO personDTO) {
        Person person = new Person();
        person.setName(personDTO.getName());
        if (personDTO.getAge() != null) {
            person.setAge(personDTO.getAge());
        }
        return modelMapper.map(personRepository.save(person), PersonResponseDTO.class);
    }

    @Transactional
    public PersonResponseDTO getPerson(Long personId){
            Person person = personRepository.findByPersonId(personId)
                    .orElseThrow(() -> new PersonNotFoundException("Person not found"));

            return modelMapper.map(person, PersonResponseDTO.class);
    }

    @Transactional
    public List<PersonResponseDTO> getAllPersons(int page, int size) {
            Pageable pageable = PageRequest.of(page, size);
            Page<Person> persons = personRepository.findAll(pageable);
            return persons.stream().map(person -> {
                PersonResponseDTO personResponseDTO = new PersonResponseDTO();
                personResponseDTO.setPersonId(person.getPersonId());
                personResponseDTO.setName(person.getName());
                personResponseDTO.setAge(person.getAge());
                return personResponseDTO;
            }).toList();
    }

    @Transactional
    public PersonResponseDTO updatePerson(Long personId, PersonUpdateDTO updatedPerson) {

            Person person = personRepository.findByPersonId(personId)
                    .orElseThrow(() -> new PersonNotFoundException("Person not found"));

            if(updatedPerson.getName() != null) person.setName(updatedPerson.getName());
            if(updatedPerson.getAge() != null) person.setAge(updatedPerson.getAge());

            person = personRepository.save(person);

            log.info("Person updated: " + person.getName() + ",age:" + person.getAge());

            return modelMapper.map(person, PersonResponseDTO.class);
    }



    @Transactional
    public void deletePerson(Long personId) {
        Person person = personRepository.findByPersonId(personId)
                .orElseThrow(() -> new PersonNotFoundException("Person not found"));

        personRepository.delete(person);
    }

    @Transactional
    public PersonMoviesResponseDTO updatePersonMovies(Long personId, PersonMoviesUpdateDTO moviesUpdateDTO) {
        Person person = personRepository.findByPersonId(personId)
                .orElseThrow(() -> new PersonNotFoundException("Person not found"));

        List<Long> movieIds = moviesUpdateDTO.getMovieIds() == null ? List.of() : moviesUpdateDTO.getMovieIds();
        if (movieIds.isEmpty()) {
            PersonMoviesResponseDTO response = new PersonMoviesResponseDTO();
            response.setPersonId(person.getPersonId());
            response.setMovieIds(List.of());
            return response;
        }

        Set<Long> requestedIds = new HashSet<>(movieIds);
        Set<Long> existingIds = person.getCast() == null ? Set.of()
                : person.getCast().stream().map(cast -> cast.getMovie().getMovieId())
                .collect(Collectors.toSet());

        // Load actual movies first so invalid IDs fail early with a clear error.
        List<Movie> requestedMovies = movieRepository.findAllById(requestedIds);
        Set<Long> foundMovieIds = requestedMovies.stream()
                .map(Movie::getMovieId)
                .collect(Collectors.toSet());

        Set<Long> missingIds = requestedIds.stream()
                .filter(id -> !foundMovieIds.contains(id))
                .collect(Collectors.toSet());
        if (!missingIds.isEmpty()) {
            throw new MovieNotFoundException("Movies not found for ids: " + missingIds);
        }

        // Add only new movie relations; keep existing person-movie mappings untouched.
        List<MovieCast> newCasts = requestedMovies.stream()
                .filter(movie -> !existingIds.contains(movie.getMovieId()))
                .map(movie -> {
                    MovieCast movieCast = new MovieCast();
                    movieCast.setMovie(movie);
                    movieCast.setPerson(person);
                    return movieCast;
                })
                .toList();

        if (!newCasts.isEmpty()) {
            if (person.getCast() == null) {
                person.setCast(new java.util.ArrayList<>());
            }
            person.getCast().addAll(newCasts);
            personRepository.save(person);
        }

        PersonMoviesResponseDTO response = new PersonMoviesResponseDTO();
        response.setPersonId(person.getPersonId());
        response.setMovieIds(newCasts.stream().map(c -> c.getMovie().getMovieId()).toList());
        return response;
    }
}
