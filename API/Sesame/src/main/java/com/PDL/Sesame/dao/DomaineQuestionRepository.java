package com.PDL.Sesame.dao;

import com.PDL.Sesame.model.DomaineQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DomaineQuestionRepository extends JpaRepository<DomaineQuestion, Long> {
   // DomaineQuestion findByNom_domaine_question(String nom_domaine_question);

 //   DomaineQuestion findByNom_domaine_question(String nom_domaine_question);


}
