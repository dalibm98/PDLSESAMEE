    package com.PDL.Sesame.auth;
    import com.PDL.Sesame.config.ResourceNotFoundException;
    import com.PDL.Sesame.dao.*;
    import com.PDL.Sesame.model.*;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.responses.ApiResponse;
    import io.swagger.v3.oas.annotations.responses.ApiResponses;
    import io.swagger.v3.oas.annotations.security.SecurityRequirement;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.transaction.annotation.Transactional;
    import org.springframework.web.bind.annotation.*;

    import java.io.IOException;
    import java.time.LocalDateTime;
    import java.util.*;
    import java.io.File;
    import org.apache.commons.io.FileUtils;
    import org.springframework.web.multipart.MultipartFile;

    @RestController
    @RequestMapping("/api/v1/auth")
    @SecurityRequirement(name = "bearerAuth")
    @Tag(name = "Authentication", description = "Operations related to Authentication")

    public class AuthenticationController {
        private final AuthenticationService service;
        private final QuestionDao questionDao;
        private final UserDao repository;
        private final NatureQuestionRepository natureDao ;
        private final DomaineQuestionRepository domaineDao ;
        private final NotificationDao notificationDao;
        private final ReponseDao  reponseDao ;

        @Autowired
        public AuthenticationController(AuthenticationService service, QuestionDao questionDao, UserDao repository, NotificationDao notificationDao ,NatureQuestionRepository natureDao ,DomaineQuestionRepository domaineDao, ReponseDao reponseDao) {
            this.service = service;
            this.questionDao = questionDao;
            this.repository = repository;
            this.notificationDao = notificationDao;
            this.natureDao = natureDao ;
            this.domaineDao =domaineDao ;
            this.reponseDao = reponseDao ;
        }
        @PostMapping("/register")
        @Operation(summary = "Inscription USER")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "User created"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")})
        public ResponseEntity<AuthenticationResponse> register(
                @RequestBody RegisterRequest request
        ) {
            return ResponseEntity.ok(service.register(request));
        }

        @PostMapping("/userss")
        public ResponseEntity<AuthenticationResponse> addNewUser(@RequestBody RegisterRequest request) {
            var response = service.addNewUser(request);
            return ResponseEntity.ok(response);
        }

        @PostMapping("/authenticate")
        @Operation(summary = "login user ")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "User authetifier avec succées"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")})
        public ResponseEntity<AuthenticationResponse> authenticate(
                @RequestBody AuthenticationRequest request
        ) {
            return ResponseEntity.ok(service.authenticate(request));
        }
        @PostMapping("/change-password")
        public ResponseEntity<AuthenticationResponse> changePassword(@RequestBody Map<String, String> newPasswordMap) {
            String newPassword = newPasswordMap.get("newPassword");
            AuthenticationResponse response = service.changePassword(newPassword);
            return ResponseEntity.ok(response);
        }

        @PostMapping("/questions")
        @Operation(summary = "Add a new question")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Question created"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")})
        public ResponseEntity<User> addQuestion(@RequestBody Question question) {
            User user =service.addQuestion(question);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(user);
        }
        @PostMapping("/users/{userId}")
        public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user) throws IOException {
            // Obtenir l'utilisateur actuellement connecté
            User currentUser = service.getCurrentUser();

            // Vérifier que l'utilisateur connecté est bien l'utilisateur que l'on souhaite mettre à jour
            if (!currentUser.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Récupérer l'utilisateur à partir de la base de données
            User existingUser = repository.findById(userId).orElse(null);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            // Mettre à jour les propriétés de l'utilisateur
            existingUser.setFirstname(user.getFirstname());
            existingUser.setLastname(user.getLastname());
            existingUser.setEmail(user.getEmail());
            existingUser.setImageUrl(user.getImageUrl());

            // Enregistrer le fichier dans la base de données
            if (user.getImage() != null) {
                String filename = "user_" + userId + ".jpg";
                FileUtils.writeByteArrayToFile(new File(filename), user.getImage());
                existingUser.setImageUrl(filename);
            }
            // Enregistrer les modifications dans la base de données
            repository.save(existingUser);
            return ResponseEntity.ok().build();
        }

        @GetMapping("/users/{userId}/image")
        public ResponseEntity<byte[]> getUserImage(@PathVariable Long userId) throws IOException {
            // Récupérer l'utilisateur à partir de la base de données
            User existingUser = repository.findById(userId).orElse(null);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }
            // Lire les données de l'image à partir du fichier
            String filename = "user_" + userId + ".jpg";
            byte[] imageData = FileUtils.readFileToByteArray(new File(filename));

            // Renvoyer les données de l'image dans la réponse HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            headers.setContentLength(imageData.length);
            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
        }

        @PostMapping("/questions/{questionId}/reponses")

        @Operation(summary = "Add a  new reponses")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Reponse ajouter avec succées"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")})
        public ResponseEntity<User> addReponse(@PathVariable Long questionId, @RequestBody Reponse reponse) {
            User user = service.addReponse(questionId, reponse);
            if (user == null) {
                return ResponseEntity.notFound().build(); // retourne une réponse HTTP 404 si l'utilisateur n'est pas trouvé
            }

            // Envoyer une notification à l'utilisateur qui a posé la question
            Question question = questionDao.findById(questionId).orElse(null);
            if (question != null && question.getAuteur() != null && !question.getAuteur().equals(user)) {
                //String message = String.format("La question '%s' a une nouvelle réponse.", question.getSujet());
                String message = String.format("La question '%s' a une nouvelle réponse ajoutée par '%s'.", question.getSujet(), user.getUsername());
                Notification notification = new Notification(message, false, new Date(), question.getAuteur());
                notificationDao.save(notification);
            }
            return ResponseEntity.ok(user);
        }
        @GetMapping("/current-user")
        @Operation(summary = "Get user connecter actuel")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "User connecteé"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")})
        public ResponseEntity<User> getCurrentUser() {
            User user = service.getCurrentUser();
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        }
        @PutMapping("/notifications/{notificationId}")
        @Operation(summary = "faire vue a la notifcation")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Vue"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")})
        public ResponseEntity<Notification> markNotificationAsRead(@PathVariable Long notificationId) {
            User user = service.getCurrentUser();
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            Notification notification = user.getNotifications().stream().filter(n -> n.getId_notification().equals(notificationId)).findFirst().orElse(null);
            if (notification == null) {
                return ResponseEntity.notFound().build();
            }

            notification.setEstLu(true);
            notificationDao.save(notification); // save the update to the database

            return ResponseEntity.ok(notification);
        }


        @GetMapping("/notifications")
        @Operation(summary = "Get all notification par user ")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "all notification"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")})
        public ResponseEntity<List<Notification>> getNotifications() {
            User user = service.getCurrentUser();
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user.getNotifications());
        }

        @GetMapping("/questions-with-reponses")
        @Operation(summary = "Get all question avec reponses")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = ""),
                @ApiResponse(responseCode = "401", description = "Unauthorized")})
        public ResponseEntity<List<QuestionWithReponses>> getAllQuestionsWithReponses() {
            return ResponseEntity.ok(service.getAllQuestionsWithReponses());
        }

            @PutMapping("/{reponseId}/vote")
            @Operation(summary = "Voter une Reponse de user a la question")
            @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Reponse Votée"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")})
            public ResponseEntity<Void> voteForReponse(@PathVariable Long reponseId) {
                service.voteForReponse(reponseId);
                return ResponseEntity.ok().build();
            }

            @GetMapping("/questions/{questionId}/classement")
            @Operation(summary = "Get all Classement Question par Vote ")
            @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Vote de question "),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")})
            public ResponseEntity<List<Reponse>> getReponsesTrieParVotes(@PathVariable Long questionId) {
                List<Reponse> reponses = service.getReponsesTrieParVotes(questionId);
                return ResponseEntity.ok().body(reponses);
            }

        @GetMapping("/users/{userId}/meilleures-reponses")
        @Operation(summary = "Get all meilleures-reponses par User ")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "meilleures-reponses"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")})
        @PreAuthorize("hasRole('ROLE_USER')")
        public ResponseEntity<List<Reponse>> getMeilleuresReponsesParUser(@PathVariable Long userId) {
            List<Reponse> reponses = service.getMeilleuresReponsesParUser(userId);
            return ResponseEntity.ok().body(reponses);
        }


        @GetMapping("/reponses/meilleures")
        @Operation(summary = "Get all meilleures-reponses pour tous les users  dans le base  ")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "meilleures-reponses pour tous les users "),
                @ApiResponse(responseCode = "401", description = "Unauthorized")})
        @PreAuthorize("hasRole('ROLE_USER')")
        public ResponseEntity<List<Reponse>> getMeilleuresReponsesTrieParVotes() {
            List<Reponse> reponses = service.getMeilleuresReponsesTrieParVotes();
            return ResponseEntity.ok().body(reponses);
        }


        @GetMapping("/users/stats")
        @Operation(summary = "Get statistics about users")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Statistics returned"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")
        })
        public ResponseEntity<List<UserStats>> getUserStats() {
            List<User> users = repository.findAll();
            List<UserStats> stats = new ArrayList<>();

            for (User user : users) {
                Long questionCount = questionDao.countByAuteur(user);
                Long reponseCount = reponseDao.countByAuteur(user);
                stats.add(new UserStats(user.getUsername(), questionCount, reponseCount));
            }

            return ResponseEntity.ok(stats);
        }
        @GetMapping("/{userId}/stats")
        public ResponseEntity<UserStats> getUserStats(@PathVariable Long userId) {
            User user = repository.findById(userId).orElseThrow(() ->new ResourceNotFoundException("User not found"));

            Long questionCount = questionDao.countByAuteur(user);
            Long reponseCount = reponseDao.countByAuteur(user);
            UserStats stats = new UserStats(user.getFirstname(), questionCount, reponseCount);
            return ResponseEntity.ok(stats);
        }
        @PutMapping("/questions/{questionId}")
        @Operation(summary = "Modify a question")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Question updated"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "404", description = "Question not found")})
        public ResponseEntity<?> modifyQuestion(@PathVariable Long questionId, @RequestBody Question question) {
            User currentUser = service.getCurrentUser();
            Question existingQuestion = questionDao.findById(questionId).orElse(null);
            if (existingQuestion == null) {
                return ResponseEntity.notFound().build();
            }
            if (!existingQuestion.getAuteur().equals(currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            existingQuestion.setSujet(question.getSujet());
            existingQuestion.setContenu(question.getContenu());
            existingQuestion.setNature(question.getNature());
            existingQuestion.setDomaine(question.getDomaine());
            questionDao.save(existingQuestion);
            return ResponseEntity.ok().build();
        }

        @PutMapping("/reponses/{reponseId}")
        @Operation(summary = "Modify an answer")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Answer updated"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "404", description = "Answer not found")})
        public ResponseEntity<?> modifyReponse(@PathVariable Long reponseId, @RequestBody Reponse reponse) {
            User currentUser = service.getCurrentUser();
            Reponse existingReponse = reponseDao.findById(reponseId).orElse(null);
            if (existingReponse == null) {
                return ResponseEntity.notFound().build();
            }
            if (!existingReponse.getAuteur().equals(currentUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            existingReponse.setContenu(reponse.getContenu());
            reponseDao.save(existingReponse);
            return ResponseEntity.ok().build();
        }

        @PutMapping(value = "/users/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)

        @Operation(summary = "Modify an user")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "user updated"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "404", description = "user not found")})
        public ResponseEntity<?> modifyUser(@PathVariable Long userId, @RequestParam("image_url") MultipartFile file, @ModelAttribute User user) throws IOException {
            // Obtenir l'utilisateur actuellement connecté
            User currentUser = service.getCurrentUser();

            // Vérifier que l'utilisateur connecté est bien l'administrateur ou l'utilisateur que l'on souhaite modifier
            // Récupérer l'utilisateur à partir de la base de données
            User existingUser = repository.findById(userId).orElse(null);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            // Mettre à jour les propriétés de l'utilisateur
            existingUser.setFirstname(user.getFirstname());
            existingUser.setLastname(user.getLastname());
            existingUser.setStatus(user.getStatus());
            existingUser.setDescription(user.getDescription());
            existingUser.setAdresse(user.getAdresse());



            // Enregistrer le fichier dans la base de données
            if (!file.isEmpty()) {
                String filename = "user_" + userId + ".jpg";
                FileUtils.writeByteArrayToFile(new File(filename), file.getBytes());
                existingUser.setImageUrl(filename);
            }

            // Enregistrer les modifications dans la base de données
            repository.save(existingUser);
            return ResponseEntity.ok().build();
        }

        @DeleteMapping("/users/{userId}")
        @Operation(summary = "delete user ")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "User deleted"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "404", description = "user not found not found")})
        public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
            // Obtenir l'utilisateur actuellement connecté
            User currentUser = service.getCurrentUser();

            // Vérifier que l'utilisateur connecté est bien l'administrateur ou l'utilisateur que l'on souhaite supprimer

            // Récupérer l'utilisateur à partir de la base de données
            User existingUser = repository.findById(userId).orElse(null);
            if (existingUser == null) {
                return ResponseEntity.notFound().build();
            }

            // Supprimer l'utilisateur de la base de données
            repository.delete(existingUser);

            // Supprimer l'image de profil de l'utilisateur s'il en avait une
            if (existingUser.getImageUrl() != null) {
                String filename = "user_" + userId + ".jpg";
                FileUtils.deleteQuietly(new File(filename));
            }

            // Supprimer toutes les questions et réponses de l'utilisateur
            List<Question> questions = questionDao.findByAuteur(existingUser);
            for (Question question : questions) {
                questionDao.delete(question);
            }
            List<Reponse> reponses = reponseDao.findByAuteur(existingUser);
            for (Reponse reponse : reponses) {
                reponseDao.delete(reponse);
            }

            return ResponseEntity.ok().build();
        }


        @GetMapping("/my-reponses")
        @Operation(summary = "Get my answers")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "List of my answers"),
                @ApiResponse(responseCode = "401", description = "Unauthorized")})
        public ResponseEntity<List<Reponse>> getMyReponses() {
            User currentUser = service.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.notFound().build();
            }
            List<Reponse> reponses = reponseDao.findByAuteur(currentUser);
            return ResponseEntity.ok(reponses);
        }


        @DeleteMapping("/questions/{questionId}")
        @Operation(summary = "Delete a question and its answers")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "204", description = "Question and its answers deleted"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "404", description = "Question not found")})
        public ResponseEntity<?> deleteQuestionAndAnswers(@PathVariable Long questionId) {
            User currentUser = service.getCurrentUser();
            Question existingQuestion = questionDao.findById(questionId).orElse(null);
            if (existingQuestion == null) {
                return ResponseEntity.notFound().build();
            }
            /*
            if (!existingQuestion.getAuteur().equals(currentUser)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

             */
            List<Reponse> reponses = reponseDao.findByQuestion(existingQuestion);
            reponseDao.deleteAll(reponses);
            questionDao.delete(existingQuestion);
            return ResponseEntity.noContent().build();

        }


        @DeleteMapping("/questions/{questionId}/reponses/{reponseId}")
        @Operation(summary = "Delete an answer")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "204", description = "Answer deleted"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "404", description = "Answer not found")})
        public ResponseEntity<?> deleteAnswer(@PathVariable Long questionId, @PathVariable Long reponseId) {
            User currentUser = service.getCurrentUser();
            Question question = questionDao.findById(questionId).orElse(null);
            if (question == null) {
                return ResponseEntity.notFound().build();
            }
            Reponse reponse = reponseDao.findById(reponseId).orElse(null);
            if (reponse == null) {
                return ResponseEntity.notFound().build();
            }
            /*
            if (!question.getAuteur().equals(currentUser) && !reponse.getAuteur().equals(currentUser)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            */

            if (!reponse.getQuestion().equals(question)) {
                return ResponseEntity.notFound().build();
            }
            reponseDao.delete(reponse);
            return ResponseEntity.noContent().build();
        }


    }






