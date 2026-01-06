package com.example.bookmyshow.services;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.MovieDTO.PersonDTO.PersonResponseDTO;
import com.example.bookmyshow.models.Person;
import com.example.bookmyshow.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    @Transactional
    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    @Transactional
    public ResponseEntity<ApiResponse<PersonResponseDTO>> getPerson(long personId){
        try {
            Person person = personRepository.findByPersonId((int) personId)
                    .orElseThrow(() -> new Exception("Person not found"));

            List<String> movies = person.getCast().stream()
                    .map(movie -> movie.getMovie().getTitle())
                    .toList();

            PersonResponseDTO personResponseDTO = new PersonResponseDTO();
            personResponseDTO.setPersonId(person.getPersonId());
            personResponseDTO.setName(person.getName());
            personResponseDTO.setAge(person.getAge());
            personResponseDTO.setMovies(movies);

            return ResponseEntity.ok(new ApiResponse<>(
                    200,
                    "✅ Person found",
                    personResponseDTO,
                    LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400).body(new ApiResponse<>(
                    400,
                    "❌ Person is not found !" + e.getMessage(),
                    null,
                    LocalDateTime.now()
            ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<List<PersonResponseDTO>>> getAllPersons() {
        try {
            List<Person> persons = (List<Person>) personRepository.findAll();
            List<PersonResponseDTO> personResponseDTOS = persons.stream().map(person -> {
                List<String> movies = person.getCast().stream()
                        .map(movie -> movie.getMovie().getTitle())
                        .toList();
                PersonResponseDTO personResponseDTO = new PersonResponseDTO();
                personResponseDTO.setPersonId(person.getPersonId());
                personResponseDTO.setName(person.getName());
                personResponseDTO.setAge(person.getAge());
                personResponseDTO.setMovies(movies);
                return personResponseDTO;
            }).toList();

            return ResponseEntity.ok(new ApiResponse<>(
                    200,
                    "✅ All Persons found",
                    personResponseDTOS,
                    LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400).body(new ApiResponse<>(
                    400,
                    "❌ Persons are not found !" + e.getMessage(),
                    null,
                    LocalDateTime.now()
            ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<PersonResponseDTO>> updatePerson(long personId, Person updatedPerson) {
        try {
            Person person = personRepository.findByPersonId(personId)
                    .orElseThrow(() -> new Exception("Person not found"));

            if(updatedPerson.getName() != null) person.setName(updatedPerson.getName());
            if(updatedPerson.getAge()!= 0) person.setAge(updatedPerson.getAge());

            person = personRepository.save(person);

            log.info("Person updated: " + person.getName() + ",age:" + person.getAge());

            List<String> movies = person.getCast().stream()
                    .map(movie -> movie.getMovie().getTitle())
                    .toList();

            PersonResponseDTO personResponseDTO = new PersonResponseDTO();
            personResponseDTO.setPersonId(person.getPersonId());
            personResponseDTO.setName(person.getName());
            personResponseDTO.setAge(person.getAge());
            personResponseDTO.setMovies(movies);

            return ResponseEntity.ok(new ApiResponse<>(
                    200,
                    "✅ Person updated",
                    personResponseDTO,
                    LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400).body(new ApiResponse<>(
                    400,
                    "❌ Person is not found !" + e.getMessage(),
                    null,
                    LocalDateTime.now()
            ));
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<PersonResponseDTO>> deletePerson(long personId) {
        try {
//            Querying and deleting person since person doesnt have much relationships
//            No need to reflect immediately
            Person person = personRepository.findByPersonId((int) personId)
                    .orElseThrow(() -> new Exception("Person not found"));

            List<String> movies = person.getCast().stream()
                    .map(movie -> movie.getMovie().getTitle())
                    .toList();
            PersonResponseDTO personResponseDTO = new PersonResponseDTO();
            personResponseDTO.setPersonId(person.getPersonId());
            personResponseDTO.setName(person.getName());
            personResponseDTO.setAge(person.getAge());
            personResponseDTO.setMovies(movies);

            personRepository.delete(person);

            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                    200,
                    "✅ Person deleted",
                    personResponseDTO,
                    LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(400).body(new ApiResponse<>(
                    400,
                    "❌ Person is not found !" + e.getMessage(),
                    null,
                    LocalDateTime.now()
            ));
        }
    }

}
