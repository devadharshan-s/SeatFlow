package com.example.bookmyshow.controllers;

import com.example.bookmyshow.DTO.ApiResponse;
import com.example.bookmyshow.DTO.MovieDTO.PersonDTO.PersonResponseDTO;
import com.example.bookmyshow.models.Person;
import com.example.bookmyshow.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PersonController {

    private final PersonService personService;

    @GetMapping("/getPerson/{personId}")
    public ResponseEntity<ApiResponse<PersonResponseDTO>> getPerson(@PathVariable long personId) {
        return personService.getPerson(personId);
    }

    @GetMapping("/getAllPersons")
    public ResponseEntity<ApiResponse<List<PersonResponseDTO>>> getAllPersons() {
        return personService.getAllPersons();
    }

    @PostMapping("/createPerson")
    public ResponseEntity<ApiResponse<Person>> createPerson(@RequestBody Person person) {
        try {
            log.info("Creating person: {}", person.getName(), person.getAge());
            Person savedPerson = personService.savePerson(person);
            return ResponseEntity.status(200)
                    .body(new ApiResponse<>(
                            200,
                            "✅ Person added to DB Successfully with Id: " + savedPerson.getPersonId(),
                            savedPerson,
                            LocalDateTime.now()
                    ));
        } catch(Exception e){
            log.error("Person is not craeted! " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "❌ Person is not created !",
                            null,
                            LocalDateTime.now()
                    ));
        }

    }

    @PatchMapping("/updatePerson/{personId}")
    public ResponseEntity<ApiResponse<PersonResponseDTO>> updatePerson
            (@RequestBody Person person, @PathVariable long personId) {
        return personService.updatePerson(personId,person);
    }

    @DeleteMapping("/deletePerson/{personId}")
    public ResponseEntity<ApiResponse<PersonResponseDTO>> deletePerson(@PathVariable long personId) {
        return personService.deletePerson(personId);
    }
}
