package com.PDL.Sesame.dao;

import com.PDL.Sesame.model.Message;
import com.PDL.Sesame.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderOrRecipientOrderByCreatedAtDesc(User sender, User recipient);
    List<Message> findBySenderAndRecipientOrSenderAndRecipientOrderByCreatedAtDesc(User sender1, User recipient1, User sender2, User recipient2);
}
