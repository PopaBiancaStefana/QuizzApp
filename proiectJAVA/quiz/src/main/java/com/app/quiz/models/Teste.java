package com.app.quiz.models;

import javax.persistence.*;

@Entity
@Table(name = "TESTE")
public class Teste {
    @Id
    @Column(name = "EMAIL" )
    private String email;
    @Basic
    @Column(name = "Q_NR")
    private Byte qNr;
    @Basic
    @Column(name = "Q_ID")
    private String qId;
    @Basic
    @Column(name = "ANS1_ID")
    private String ans1Id;
    @Basic
    @Column(name = "ANS2_ID")
    private String ans2Id;
    @Basic
    @Column(name = "ANS3_ID")
    private String ans3Id;
    @Basic
    @Column(name = "ANS4_ID")
    private String ans4Id;
    @Basic
    @Column(name = "ANS5_ID")
    private String ans5Id;
    @Basic
    @Column(name = "ANS6_ID")
    private String ans6Id;
    @Basic
    @Column(name = "CORRECT_ANS_ID")
    private String correctAnsId;
    @Basic
    @Column(name = "FINAL_ANS_ID")
    private String finalAnsId;


//    @ManyToOne
//    @JoinColumn(name = "EMAIL" , referencedColumnName = "EMAIL")
//    private Person person;

//    public Person getPerson() {
//        return person;
//    }
//
//    public void setPerson(Person person) {
//        this.person = person;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Byte getqNr() {
        return qNr;
    }

    public void setqNr(Byte qNr) {
        this.qNr = qNr;
    }

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }

    public String getAns1Id() {
        return ans1Id;
    }

    public void setAns1Id(String ans1Id) {
        this.ans1Id = ans1Id;
    }

    public String getAns2Id() {
        return ans2Id;
    }

    public void setAns2Id(String ans2Id) {
        this.ans2Id = ans2Id;
    }

    public String getAns3Id() {
        return ans3Id;
    }

    public void setAns3Id(String ans3Id) {
        this.ans3Id = ans3Id;
    }

    public String getAns4Id() {
        return ans4Id;
    }

    public void setAns4Id(String ans4Id) {
        this.ans4Id = ans4Id;
    }

    public String getAns5Id() {
        return ans5Id;
    }

    public void setAns5Id(String ans5Id) {
        this.ans5Id = ans5Id;
    }

    public String getAns6Id() {
        return ans6Id;
    }

    public void setAns6Id(String ans6Id) {
        this.ans6Id = ans6Id;
    }

    public String getCorrectAnsId() {
        return correctAnsId;
    }

    public void setCorrectAnsId(String correctAnsId) {
        this.correctAnsId = correctAnsId;
    }

    public String getFinalAnsId() {
        return finalAnsId;
    }

    public void setFinalAnsId(String finalAnsId) {
        this.finalAnsId = finalAnsId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Teste that = (Teste) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (qNr != null ? !qNr.equals(that.qNr) : that.qNr != null) return false;
        if (qId != null ? !qId.equals(that.qId) : that.qId != null) return false;
        if (ans1Id != null ? !ans1Id.equals(that.ans1Id) : that.ans1Id != null) return false;
        if (ans2Id != null ? !ans2Id.equals(that.ans2Id) : that.ans2Id != null) return false;
        if (ans3Id != null ? !ans3Id.equals(that.ans3Id) : that.ans3Id != null) return false;
        if (ans4Id != null ? !ans4Id.equals(that.ans4Id) : that.ans4Id != null) return false;
        if (ans5Id != null ? !ans5Id.equals(that.ans5Id) : that.ans5Id != null) return false;
        if (ans6Id != null ? !ans6Id.equals(that.ans6Id) : that.ans6Id != null) return false;
        if (correctAnsId != null ? !correctAnsId.equals(that.correctAnsId) : that.correctAnsId != null) return false;
        if (finalAnsId != null ? !finalAnsId.equals(that.finalAnsId) : that.finalAnsId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = email != null ? email.hashCode() : 0;
        result = 31 * result + (qNr != null ? qNr.hashCode() : 0);
        result = 31 * result + (qId != null ? qId.hashCode() : 0);
        result = 31 * result + (ans1Id != null ? ans1Id.hashCode() : 0);
        result = 31 * result + (ans2Id != null ? ans2Id.hashCode() : 0);
        result = 31 * result + (ans3Id != null ? ans3Id.hashCode() : 0);
        result = 31 * result + (ans4Id != null ? ans4Id.hashCode() : 0);
        result = 31 * result + (ans5Id != null ? ans5Id.hashCode() : 0);
        result = 31 * result + (ans6Id != null ? ans6Id.hashCode() : 0);
        result = 31 * result + (correctAnsId != null ? correctAnsId.hashCode() : 0);
        result = 31 * result + (finalAnsId != null ? finalAnsId.hashCode() : 0);
        return result;
    }


}
