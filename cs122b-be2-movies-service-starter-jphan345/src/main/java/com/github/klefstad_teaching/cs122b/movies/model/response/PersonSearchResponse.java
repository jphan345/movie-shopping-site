package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.model.data.PersonDetails;

import java.util.List;

public class PersonSearchResponse extends ResponseModel<PersonSearchResponse> {
    List<PersonDetails> persons;

    public List<PersonDetails> getPersons() {
        return persons;
    }

    public PersonSearchResponse setPersons(List<PersonDetails> persons) {
        this.persons = persons;
        return this;
    }
}
