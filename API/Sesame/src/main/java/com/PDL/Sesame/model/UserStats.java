package com.PDL.Sesame.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Table(name = "UserStats")
public class UserStats {

    private String firstname;
    private Long questionCount;
    private Long reponseCount;

    public UserStats(String firstname, Long questionCount, Long reponseCount) {
        this.firstname = firstname;
        this.questionCount = questionCount;
        this.reponseCount = reponseCount;
    }

    public String getUsername() {
        return firstname;
    }

    public void setUsername(String firstname) {
        this.firstname = firstname;
    }

    public Long getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Long questionCount) {
        this.questionCount = questionCount;
    }

    public Long getReponseCount() {
        return reponseCount;
    }

    public void setReponseCount(Long reponseCount) {
        this.reponseCount = reponseCount;
    }
}
