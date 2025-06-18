package com.task.BiteSpeed.services;

import com.task.BiteSpeed.dto.ContactRequestDTO;
import com.task.BiteSpeed.dto.ContactResponseDTO;
import com.task.BiteSpeed.enums.LinkPrecedence;
import com.task.BiteSpeed.model.Contact;
import com.task.BiteSpeed.respository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ContactService {

    @Autowired
    ContactRepository contactRepo;

    public ContactResponseDTO addContact(ContactRequestDTO input) {
        List<Contact> matches = findContacts(input);

        if (matches.isEmpty()) {
            return createFirstContact(input);
        }
        Set<Contact> allMatches = getAllContacts(matches);

        if (checkNewCombo(input, allMatches)) {
            Contact prime = getMainContact(allMatches);
                addNewSecondary(input, prime);
            allMatches = getAllContacts(List.of(prime));
        }

        fixMultiplePrimaries(allMatches);

         Contact primary = getMainContact(allMatches);
        allMatches = getAllContacts(List.of(primary));

        return prepareResult(allMatches);
    }

    private List<Contact> findContacts(ContactRequestDTO input) {
         List<Contact> found = new ArrayList<>();
         if (input.getEmail() != null) {
                found.addAll(contactRepo.findAllByEmail(input.getEmail()));
        }
        if (input.getPhoneNumber() != null) {
            found.addAll(contactRepo.findAllByPhoneNumber(input.getPhoneNumber()));
        }

        return new ArrayList<>(new HashSet<>(found));
    }
    private ContactResponseDTO createFirstContact(ContactRequestDTO input) {
        Contact newOne = new Contact();
        newOne.setEmail(input.getEmail());
        newOne.setPhoneNumber(input.getPhoneNumber());
        newOne.setLinkedId(null);
        newOne.setLinkPrecedence(LinkPrecedence.primary);
        newOne.setCreatedAt(LocalDateTime.now());
        newOne.setUpdatedAt(LocalDateTime.now());

         Contact saved = contactRepo.save(newOne);

            ContactResponseDTO result = new ContactResponseDTO();
        result.setPrimaryContactId(saved.getId());

        List<String> emails = new ArrayList<>();
          if (saved.getEmail() != null) emails.add(saved.getEmail());
             result.setEmails(emails);

            List<String> phones = new ArrayList<>();
        if (saved.getPhoneNumber() != null) phones.add(saved.getPhoneNumber());
            result.setPhoneNumbers(phones);
            result.setSecondaryContactIds(new ArrayList<>());
            return result;
    }
    private Set<Contact> getAllContacts(List<Contact> someContacts) {
        Set<Contact> group = new HashSet<>();

         for (Contact c : someContacts) {
            Contact main = findMain(c);
            group.add(main);
            group.addAll(contactRepo.findAllByLinkedId(main.getId()));
        }

        return group;
    }

    private Contact findMain(Contact c) {
        if (c.getLinkPrecedence() == LinkPrecedence.primary) return c;
          return contactRepo.findById(c.getLinkedId()).orElse(c);
    }
    private boolean checkNewCombo(ContactRequestDTO input, Set<Contact> existing) {
        for (Contact c : existing) {
            boolean sameEmail = Objects.equals(c.getEmail(), input.getEmail());
              boolean samePhone = Objects.equals(c.getPhoneNumber(), input.getPhoneNumber());

             if (sameEmail && samePhone) return false;
        }
        return true;
    }

    private void addNewSecondary(ContactRequestDTO input, Contact main) {
        Contact extra = new Contact();
         extra.setEmail(input.getEmail());
        extra.setPhoneNumber(input.getPhoneNumber());
        extra.setLinkedId(main.getId());
        extra.setLinkPrecedence(LinkPrecedence.secondary);
        extra.setCreatedAt(LocalDateTime.now());
        extra.setUpdatedAt(LocalDateTime.now());

         contactRepo.save(extra);
    }

    private void fixMultiplePrimaries(Set<Contact> contacts) {
        List<Contact> primaries = new ArrayList<>();
         for (Contact c : contacts) {
            if (c.getLinkPrecedence() == LinkPrecedence.primary) {
                primaries.add(c);
            }
        }if (primaries.size() <= 1) return;

        primaries.sort(Comparator.comparing(Contact::getCreatedAt));
        Contact main = primaries.get(0);
         for (int i = 1; i < primaries.size(); i++) {
            Contact toChange = primaries.get(i);
            toChange.setLinkPrecedence(LinkPrecedence.secondary);
            toChange.setLinkedId(main.getId());
            toChange.setUpdatedAt(LocalDateTime.now());
            contactRepo.save(toChange);
             List<Contact> secondaries = contactRepo.findAllByLinkedId(toChange.getId());
             for (Contact sc : secondaries) {
                sc.setLinkedId(main.getId());
                sc.setUpdatedAt(LocalDateTime.now());
                contactRepo.save(sc);
            }
        }
    }

    private Contact getMainContact(Set<Contact> set) {
        for (Contact c : set) {
             if (c.getLinkPrecedence() == LinkPrecedence.primary) return c;
        }
        return null;
    }
    private ContactResponseDTO prepareResult(Set<Contact> fullSet) {
        Contact main = getMainContact(fullSet);

        List<Contact> secondaries = new ArrayList<>();
        for (Contact c : fullSet) {
            if (c.getLinkPrecedence() == LinkPrecedence.secondary) {
                secondaries.add(c);
            }
        }
        secondaries.sort(Comparator.comparing(Contact::getCreatedAt));
        Set<String> emails = new LinkedHashSet<>();
        Set<String> phones = new LinkedHashSet<>();
        if (main.getEmail() != null) emails.add(main.getEmail());
         if (main.getPhoneNumber() != null) phones.add(main.getPhoneNumber());

        for (Contact c : secondaries) {
            if (c.getEmail() != null) emails.add(c.getEmail());
            if (c.getPhoneNumber() != null) phones.add(c.getPhoneNumber());
        }
        ContactResponseDTO out = new ContactResponseDTO();
         out.setPrimaryContactId(main.getId());
        out.setEmails(new ArrayList<>(emails));
        out.setPhoneNumbers(new ArrayList<>(phones));

        List<Integer> secIds = new ArrayList<>();
        for (Contact c : secondaries) {
             secIds.add(c.getId());
        }
        out.setSecondaryContactIds(secIds);
        return out;
    }
}
