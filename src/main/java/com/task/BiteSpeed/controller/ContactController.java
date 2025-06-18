package com.task.BiteSpeed.controller;

import com.task.BiteSpeed.dto.ContactRequestDTO;
import com.task.BiteSpeed.dto.ContactResponseDTO;
import com.task.BiteSpeed.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {

    @Autowired
    ContactService contactService;


    @PostMapping("/identify")
    public ContactResponseDTO addContact(@RequestBody ContactRequestDTO requestDTO){
        return contactService.addContact(requestDTO);
    }
}
