package com.app.quiz.models;

import javax.persistence.*;

@Entity
@Table(name = "RASPUNSURI")
public class Raspunsuri {
    @Basic
    @Column(name = "Q_ID")
    private String qId;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private String id;
    @Basic
    @Column(name = "TEXT_RASPUNS")
    private String textRaspuns;
    @Basic
    @Column(name = "CORECT")
    private String corect;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Raspunsuri that = (Raspunsuri) o;

        if (qId != null ? !qId.equals(that.qId) : that.qId != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (textRaspuns != null ? !textRaspuns.equals(that.textRaspuns) : that.textRaspuns != null) return false;
        if (corect != null ? !corect.equals(that.corect) : that.corect != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = qId != null ? qId.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (textRaspuns != null ? textRaspuns.hashCode() : 0);
        result = 31 * result + (corect != null ? corect.hashCode() : 0);
        return result;
    }
}
