package com.example.bookmyshow.movie.api;

import com.example.bookmyshow.dto.ApiResponse;
import com.example.bookmyshow.movie.api.dto.person.*;
import com.example.bookmyshow.movie.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<ApiResponse<PersonResponseDTO>> getPerson(@PathVariable Long personId) {
        PersonResponseDTO responseDTO = personService.getPerson(personId);

        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "✅ Person found",
                responseDTO,
                LocalDateTime.now()
        ));
    }

    @GetMapping("/getAllPersons")
    public ResponseEntity<ApiResponse<List<PersonResponseDTO>>> getAllPersons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PersonResponseDTO> responseDTOs = personService.getAllPersons(page, size);
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "âœ… All Persons found",
                responseDTOs,
                LocalDateTime.now()
        ));
    }

    @PostMapping("/createPerson")
    public ResponseEntity<ApiResponse<PersonResponseDTO>> createPerson(@RequestBody PersonCreateDTO person) {
            log.info("Creating person: {}", person.getName(), person.getAge());
            PersonResponseDTO savedPerson = personService.savePerson(person);

            return ResponseEntity.ok()
                    .body(new ApiResponse<>(
                            200,
                            "✅ Person added to DB Successfully with Id: " + savedPerson.getPersonId(),
                            savedPerson,
                            LocalDateTime.now()
                    ));
    }

    @PatchMapping("/updatePerson/{personId}")
    public ResponseEntity<ApiResponse<PersonResponseDTO>> updatePerson
            (@RequestBody PersonUpdateDTO person, @PathVariable Long personId) {
        PersonResponseDTO responseDTO = personService.updatePerson(personId, person);
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "âœ… Person updated",
                responseDTO,
                LocalDateTime.now()
        ));
    }

    @PatchMapping("/person/{personId}/movies")
    public ResponseEntity<ApiResponse<PersonMoviesResponseDTO>> updatePersonMovies(
            @PathVariable Long personId,
            @RequestBody PersonMoviesUpdateDTO moviesUpdateDTO) {
        PersonMoviesResponseDTO responseDTO = personService.updatePersonMovies(personId, moviesUpdateDTO);
        return ResponseEntity.ok(new ApiResponse<>(
                200,
                "✅ Person movies updated",
                responseDTO,
                LocalDateTime.now()
        ));
    }

    @DeleteMapping("/deletePerson/{personId}")
    public ResponseEntity<ApiResponse<?>> deletePerson(@PathVariable Long personId) {
        personService.deletePerson(personId);
        return ResponseEntity.ok(
                new ApiResponse<>(
                        200,
                        "✅ Person deleted",
                        "Person Id: " + personId,
                        LocalDateTime.now()
                )
        );
    }
}






