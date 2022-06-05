package com.app.quiz.controllers;

import com.app.quiz.models.Person;

public class ResponseType {
    private Person person;
    private String response;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
