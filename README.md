# BiteSpeed Task

## Overview

This project is a contact identification and linkage service built using Java and Spring Boot. It uses PostgreSQL as the database and is hosted on Render.com. The service exposes a single API endpoint to identify and link contacts based on phone numbers and emails.

---

## Tech Stack

- **Backend:** Java, Spring Boot
- **Database:** PostgreSQL
- **Hosting:** Render.com
- **API Endpoint:** `https://bitespeed-task-i267.onrender.com/identify`

---

## Database Schema

The project uses a single table named `Contact` with the following structure:


- **id**: Unique identifier for each contact.
- **phoneNumber**: The contact’s phone number.
- **email**: The contact’s email address.
- **linkedId**: Reference to another contact’s `id` if this contact is linked to a primary contact.
- **linkPrecedence**: Indicates whether the contact is a `primary` or `secondary` record.
- **createdAt** & **updatedAt**: Timestamps for record creation and last update.
- **deletedAt**: Timestamp for soft deletion (nullable).


The implementation passes all provided test cases and follows the problem statement closely.

---


