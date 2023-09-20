package com.github.klefstad_teaching.cs122b.movies.model.response;

import com.github.klefstad_teaching.cs122b.core.base.ResponseModel;
import com.github.klefstad_teaching.cs122b.movies.model.data.PersonDetails;

public class PersonByIdResponse extends ResponseModel<PersonByIdResponse> {
    PersonDetails person;

    public PersonDetails getPerson() {
        return person;
    }

    public PersonByIdResponse setPerson(PersonDetails person) {
        this.person = person;
        return this;
    }
}
