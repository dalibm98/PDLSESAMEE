package com.PDL.Sesame.controleur;

import com.PDL.Sesame.model.DomaineQuestion;
import com.PDL.Sesame.service.DomaineQuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;




@RestController
@RequestMapping("/api/domaine-questions")
@Tag(name = "Domaine question", description = "Operations related to domaine questions")
public class DomaineQuestionController {

    @Autowired
    private DomaineQuestionService domaineQuestionService;


    @GetMapping
    @Operation(summary = "Get all DomaineQuestions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Domaine"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public List<DomaineQuestion> getAllDomaineQuestions() {
        return domaineQuestionService.getAllDomaineQuestions();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get all DomaineQuestions BY ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Domaine created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public DomaineQuestion getDomaineQuestionById(@PathVariable Long id) {
        return domaineQuestionService.getDomaineQuestionById(id);
    }


    @PostMapping
    @Operation(summary = "Creé  DomaineQuestions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Domaine created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public DomaineQuestion createDomaineQuestion(@RequestBody DomaineQuestion domaineQuestion) {
        return domaineQuestionService.createDomaineQuestion(domaineQuestion);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Modifier DomaineQuestions By ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Domaine Modifer avec Sucées"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public DomaineQuestion updateDomaineQuestion(@PathVariable Long id, @RequestBody DomaineQuestion domaineQuestion) {
        return domaineQuestionService.updateDomaineQuestion(id, domaineQuestion);
    }


    @DeleteMapping("/{id}")

    @Operation(summary = "Delete a DomaineQuestion by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deletes a DomaineQuestion object by its ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public void deleteDomaineQuestion(@PathVariable Long id) {
        domaineQuestionService.deleteDomaineQuestion(id);
    }
}
