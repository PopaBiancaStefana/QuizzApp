package com.app.quiz.controllers;

import com.app.quiz.models.Person;
import com.app.quiz.service.PersonService;
import com.app.quiz.statistics.Statistics;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500", maxAge = 3600)
@RequestMapping("api/v1/quiz")
public class PersonController {
    @Autowired
    private PersonService personService;

    @GetMapping(
            value = "/test"
    )
    public @ResponseBody byte[] getImageWithMediaType() throws IOException {
        InputStream in = getClass()
                .getResourceAsStream("/network.svg");
        return IOUtils.toByteArray(in);
    }

    @GetMapping(value = "/users")
    public ResponseEntity<Map< String, List<String>>> getPersons() {
        List<Person> people = personService.findAll();
        Statistics stats = new Statistics(people);
        stats.calculateStatistics();

        return new ResponseEntity<>(stats.getPeopleDomains(), new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/{email}")
    public ResponseEntity<Person> getPerson(@PathVariable("email") String email) {
        return ResponseEntity.ok(personService.findById(email).orElseThrow(ResourceNotFoundException::new));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        //gnerate hashcode for email adress
        person.setHashcode();
        //save to database
        Person createdPerson = personService.save(person);
        if (createdPerson == null)
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(createdPerson, new HttpHeaders(), HttpStatus.CREATED);
    }

    @PostMapping(value = "/game", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> loginPerson(@RequestBody ResponseType responseType) {


        //email does not exist
        Person foundPerson = personService.findById(responseType.getPerson().getEmail()).orElseThrow(NoSuchElementException::new);


        //hashcode is not correct
        if (!Objects.equals(foundPerson.getHashcode(), responseType.getPerson().getHashcode())) {
            return new ResponseEntity<>("[Warning]Hashcode-ul nu exista.", new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        try {
            String resp = personService.NextQuestion(foundPerson.getEmail(), responseType.getResponse());
            if (resp.startsWith("Q")) {
                //return next question
                return new ResponseEntity<>(personService.GetQuestionAnswers(foundPerson.getEmail(), resp), new HttpHeaders(), HttpStatus.OK);
            } else {
                //return final score
                return new ResponseEntity<>(resp, new HttpHeaders(), HttpStatus.OK);
            }

        } catch (Exception e) { //catch custom exception from plsql
            return new ResponseEntity<>("[Warning] Testul poate fi rezolvat o singura data.", new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> deletePerson(@PathVariable("email") String email) {
        personService.deleteById(email);
        return new ResponseEntity<>(email, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping("/scores")
    public ResponseEntity<String> printScores() {
        return new ResponseEntity<>(personService.findScores(), new HttpHeaders(), HttpStatus.OK);
    }

}