package com.task.BiteSpeed.respository;

import com.task.BiteSpeed.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
        List<Contact> findAllByEmail(String email);
    List<Contact> findAllByPhoneNumber(String phoneNumber);

    List<Contact> findAllByLinkedId(Integer id);
}
