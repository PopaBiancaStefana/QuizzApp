package com.app.quiz.models;

import javax.persistence.*;

@Entity
@Table(name = "INTREBARI")
public class Intrebari {
    @Basic
    @Column(name = "DOMENIU")
    private String domeniu;
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "ID")
    private String id;
    @Basic
    @Column(name = "TEXT_INTREBARE")
    private String textIntrebare;

    public String getDomeniu() {
        return domeniu;
    }

    public void setDomeniu(String domeniu) {
        this.domeniu = domeniu;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTextIntrebare() {
        return textIntrebare;
    }

    public void setTextIntrebare(String textIntrebare) {
        this.textIntrebare = textIntrebare;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Intrebari that = (Intrebari) o;

        if (domeniu != null ? !domeniu.equals(that.domeniu) : that.domeniu != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (textIntrebare != null ? !textIntrebare.equals(that.textIntrebare) : that.textIntrebare != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = domeniu != null ? domeniu.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (textIntrebare != null ? textIntrebare.hashCode() : 0);
        return result;
    }
}
