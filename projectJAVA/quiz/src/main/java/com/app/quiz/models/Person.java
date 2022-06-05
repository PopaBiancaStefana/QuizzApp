package com.app.quiz.models;

import javax.persistence.*;

@Entity
@NamedStoredProcedureQuery(name = "QUIZ.NEXT_QUESTION",
        procedureName = "QUIZ.NEXT_QUESTION",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_email", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_raspuns", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_val_return", type = String.class)

        })
@NamedStoredProcedureQuery( name = "QUIZ.GET_QUESTION_ANSWERS",
    procedureName = "QUIZ.GET_QUESTION_ANSWERS",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_email", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_q_id", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_text", type = String.class)
        })
@NamedStoredProcedureQuery( name = "QUIZ.GET_SCORES",
        procedureName = "QUIZ.GET_SCORES",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.INOUT, name = "p_scores", type = String.class)
        })
@NamedStoredProcedureQuery( name = "QUIZ.CHECK_IF_PLAYER_EXISTS",
        procedureName = "QUIZ.CHECK_IF_PLAYER_EXISTS",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_email", type = String.class),
                @StoredProcedureParameter(mode = ParameterMode.OUT, name = "p_exists", type = Integer.class),
        })

@Table(name = "UTILIZATOR")
public class Person {
    @Id
    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Basic
    @Column(name = "HASHCODE")
    private Long hashcode;

    @Basic
    @Column(name = "PUNCTAJ")
    private Byte punctaj;

    @Basic
    @Column(name = "DOMENII")
    private String domenii;

//    @OneToMany(mappedBy= "person" ,fetch = FetchType.LAZY, cascade = CascadeType.ALL )
// private Set<Teste> teste;


    public Person() {
    }

    public Person(String email) {
        setEmail(email);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getHashcode() {
        return hashcode;
    }

    public void setHashcode() {
        this.hashcode = Long.valueOf(this.hashCode());
    }

    public Byte getPunctaj() {
        return punctaj;
    }

    public void setPunctaj(Byte punctaj) {
        this.punctaj = punctaj;
    }

    public String getDomenii() {
        return domenii;
    }

    public void setDomenii(String domenii) {
        this.domenii = domenii;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person that = (Person) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (hashcode != null ? !hashcode.equals(that.hashcode) : that.hashcode != null) return false;
        if (punctaj != null ? !punctaj.equals(that.punctaj) : that.punctaj != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (hashcode != null ? hashcode.hashCode() : 0);
        result = 31 * result + (punctaj != null ? punctaj.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return email;
    }
}
