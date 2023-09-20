package com.github.klefstad_teaching.cs122b.movies.model.data;

public class PersonDetails {
    private Long id;
    private String name;
    private String birthday;
    private String biography;
    private String birthplace;
    private Float popularity;
    private String profilePath;

    public Long getId() {
        return id;
    }

    public PersonDetails setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public PersonDetails setName(String name) {
        this.name = name;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public PersonDetails setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getBiography() {
        return biography;
    }

    public PersonDetails setBiography(String biography) {
        if (biography != null) {
            this.biography = biography.replace("\r","");
        }
        return this;
    }

    public String getBirthplace() {
        return birthplace;
    }

    public PersonDetails setBirthplace(String birthplace) {
        this.birthplace = birthplace;
        return this;
    }

    public Float getPopularity() {
        return popularity;
    }

    public PersonDetails setPopularity(Float popularity) {
        this.popularity = popularity;
        return this;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public PersonDetails setProfilePath(String profilePath) {
        this.profilePath = profilePath;
        return this;
    }
}
