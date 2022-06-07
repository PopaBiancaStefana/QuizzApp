package com.app.quiz.repository;


import com.app.quiz.models.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, String> {

    @Procedure( name ="QUIZ.NEXT_QUESTION")
    String NextQuestion(@Param("p_email") String email, @Param("p_raspuns") String raspuns);

    @Procedure( name ="QUIZ.GET_QUESTION_ANSWERS")
    String GetQuestionAnswers(@Param("p_email") String email, @Param("p_q_id") String q_id);

    @Procedure( name ="QUIZ.GET_SCORES")
    String GetScores(@Param("p_scores") String scores);

    @Procedure( name = "QUIZ.CHECK_IF_PLAYER_EXISTS")
    int CheckIfPlayerExists(@Param("p_email") String email);

}
