package com.PDL.Sesame.controleur;

import com.PDL.Sesame.model.NatureQuestion;
import com.PDL.Sesame.service.NatureQuestionService;
//import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/nature-questions")

@Tag(name = "Nature Question", description = "Operations related to Nature questions")
public class NatureQuestionController {

    @Autowired
    private NatureQuestionService natureQuestionService;

    @GetMapping
    @Operation(summary = "Récupérer toutes les questions de nature")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All NatureQuestion"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public List<NatureQuestion> getAllNatureQuestions() {
        return natureQuestionService.getAllNatureQuestions();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une question de nature par son id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = " une question de nature par son id avec suceés"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public ResponseEntity<NatureQuestion> getNatureQuestionById(@PathVariable Long id) {
        Optional<NatureQuestion> natureQuestion = natureQuestionService.getNatureQuestionById(id);
        if (natureQuestion.isPresent()) {
            return ResponseEntity.ok(natureQuestion.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Créer une question de nature ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Nature Question Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public NatureQuestion createNatureQuestion(@RequestBody NatureQuestion natureQuestion) {
        return natureQuestionService.saveNatureQuestion(natureQuestion);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une question de nature par son id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "une question de nature par son id Modifiée avec Succée"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public ResponseEntity<NatureQuestion> updateNatureQuestion(@PathVariable Long id, @RequestBody NatureQuestion natureQuestion) {
        Optional<NatureQuestion> existingNatureQuestion = natureQuestionService.getNatureQuestionById(id);
        if (existingNatureQuestion.isPresent()) {
            natureQuestion.setId_nature_question(id);
            return ResponseEntity.ok(natureQuestionService.saveNatureQuestion(natureQuestion));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une question de nature par son id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "nature de Question a éte supprimer avec succées"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public ResponseEntity<Void> deleteNatureQuestionById(@PathVariable Long id) {
        Optional<NatureQuestion> existingNatureQuestion = natureQuestionService.getNatureQuestionById(id);
        if (existingNatureQuestion.isPresent()) {
            natureQuestionService.deleteNatureQuestionById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
