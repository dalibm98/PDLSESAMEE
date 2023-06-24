package com.PDL.Sesame.dao;

import com.PDL.Sesame.model.Question;
import com.PDL.Sesame.model.Reponse;
import com.PDL.Sesame.model.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface ReponseDao extends JpaRepository<Reponse, Long> {

    //List<Reponse> findByQuestion(Question question);

   List<Reponse> findByAuteur(User user);

    List<Reponse> findByQuestionOrderByDateCreationAsc(Question question);

    List<Reponse> findByAuteurOrderByDateCreationAsc(User auteur);
    Long countByAuteur(User user);

    List<Reponse> findByQuestion(Question question);

    @Query("SELECT COUNT(r) FROM Reponse r WHERE r.auteur = :user")
    int countVotesByAuteur(@Param("user") User user);



}
