package com.PDL.Sesame.controleur;

import com.PDL.Sesame.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController {
    private final AuthenticationService voteService ;






    @GetMapping("commentaires/total")
    public int getNombreTotalCommentaires() {
        return voteService.getNombreTotalCommentaires();
    }

    @GetMapping("questions/total")
    public int getNombreTotalQuestions() {
        return voteService.getNombreTotalQuestions();
    }
}
