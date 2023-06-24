package com.PDL.Sesame.controleur;

import com.PDL.Sesame.auth.AuthenticationService;
import com.PDL.Sesame.dao.UserDao;
import com.PDL.Sesame.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController {
    private final AuthenticationService voteService;


    private final UserDao userDao;


    @GetMapping("commentaires/total")
    public int getNombreTotalCommentaires() {
        return voteService.getNombreTotalCommentaires();
    }

    @GetMapping("questions/total")
    public int getNombreTotalQuestions() {
        return voteService.getNombreTotalQuestions();
    }


    @GetMapping("/user/{userId}")
    public int getUserVoteCount(@PathVariable("userId") Integer userId) {
        Optional<User> userOptional = userDao.findById(Long.valueOf(userId));
        User user = userOptional.orElse(null);
        return voteService.getVoteCountByUser(user);
    }



    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public long getUserCount() {
        return voteService.countUsers();
    }
}