package com.app.quiz.service;

import com.app.quiz.models.Person;
import com.app.quiz.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class PersonService {
    @Autowired
    private PersonRepository personRepository;

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person save(Person person) {
        return personRepository.save(person);
    }

    public Optional<Person> findById( String email) {
        return personRepository.findById(email);
    }

    public String NextQuestion ( String email, String raspuns) {
        return personRepository.NextQuestion(email,raspuns);
    }

    public String GetQuestionAnswers ( String email, String q_id) {
        return personRepository.GetQuestionAnswers(email, q_id);
    }

    public void delete(Person person) {
        if (!personRepository.existsById(person.getEmail()))
            throw new ResourceNotFoundException();

        personRepository.delete(person);
    }

    public void deleteById(String email) {
        var person = personRepository.findById(email).orElseThrow(ResourceNotFoundException::new);
        personRepository.delete(person);
    }
    public String findScores() {
        String scores = "";
        return personRepository.GetScores(scores);
    }

    public int checkMail(String email){
        return personRepository.CheckIfPlayerExists(email);
    }
}
