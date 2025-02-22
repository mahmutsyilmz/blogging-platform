# Blogging Platform Backend

**Blogging Platform Backend** is a RESTful API designed to power a comprehensive blogging system. This application provides robust user management, post creation and moderation, email verification, AI-powered content summarization, and more. It leverages modern Java technologies, follows security best practices, and is built for scalability and maintainability.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
  - [User Management](#user-management)
  - [Post Management](#post-management)
  - [Like Functionality](#like-functionality)
  - [AI-Powered Content Summarization](#ai-powered-content-summarization)
  - [Admin Panel](#admin-panel)
  - [Logging & Auditing](#logging--auditing)
- [Architecture & Technologies](#architecture--technologies)
- [Configuration](#configuration)
  - [Environment Variables](#environment-variables)
  - [Running Locally](#running-locally)
- [API Endpoints](#api-endpoints)
  - [User Endpoints](#user-endpoints)
  - [Post Endpoints](#post-endpoints)
  - [Like Endpoints](#like-endpoints)
  - [Admin Endpoints](#admin-endpoints)
  - [AI Endpoints](#ai-endpoints)
- [Testing](#testing)

## Overview

The Blogging Platform Backend is a Spring Boot application that serves as the core of a dynamic blogging system. It handles user registration, authentication with JWT, post management (creation, update, deletion), admin moderation of post requests, and integrates with external AI services for content summarization. Additionally, it includes a comprehensive email verification process and extensive logging for auditing and debugging purposes.

## Features

### User Management

- **Registration & Authentication:**
  - Users can register with a unique username, email, and password.
  - Authentication is performed via JSON Web Tokens (JWT) to ensure secure, stateless sessions.
- **Email Verification:**
  - After registration or when updating an email address, users receive a verification code via email.
  - The account is marked as verified only after entering the correct code.
- **Profile Management:**
  - Users can view and update their profiles, including username, email, and bio.
  - Changing the email automatically resets the verification status.
- **Secure Endpoints:**
  - Access to sensitive endpoints is restricted using Spring Security with role-based access control.

### Post Management

- **Post Creation & Editing:**
  - Authenticated users can create new posts and submit update requests.
  - Instead of immediately applying changes, post requests are sent for admin approval.
- **Post Moderation:**
  - Admins review pending post requests (create, update, delete) and either approve or reject them.
  - Email notifications are sent to the request submitter upon decision.
- **Public Post Listing & Details:**
  - All approved posts are publicly listed.
  - Detailed views for individual posts are provided, including support for rich content formatting.

### Like Functionality

- **User Likes:**
  - Users can like or unlike posts.
  - The system tracks the like count and displays a list of users who liked each post.
- **Real-time Updates:**
  - Like counts and user-like statuses are updated in real time via API calls.

### AI-Powered Content Summarization

- **Integration with Google Gemini API:**
  - The backend integrates with Google’s Generative Language API (Gemini) to generate a concise summary (60 characters) of post content upon request.
- **Dynamic Summaries:**
  - Users can click a “Summarize” button on posts to view a short summary in a dialog window.

### Admin Panel

- **User Management:**
  - Admins have the ability to view, manage, and delete user accounts.
- **Post Request Moderation:**
  - Admins can view all pending post requests and take appropriate actions (approve/reject), with automatic email notifications sent to the affected users.
- **Dashboard & Logging:**
  - An admin dashboard displays key statistics (total users, posts, likes).
  - The system logs important actions for audit purposes.

### Logging & Auditing

- **Extensive Logging:**
  - The application logs system events, errors, and user activities using a custom logging mechanism for detailed traceability.
- **Audit Trails:**
  - Critical operations—such as user logins, post submissions, and admin actions—are recorded with timestamps to maintain a clear audit trail.
## Architecture & Technologies

- **Spring Boot:** Core framework for building the application.
- **Spring Security:** For authentication and role-based authorization using JWT.
- **Spring Data JPA & Hibernate:** For object-relational mapping and database interactions.
- **PostgreSQL:** Primary relational database.
- **JWT (JSON Web Tokens):** Used for secure, stateless session management.
- **WebClient:** To call external APIs (e.g., Google Gemini API for content summarization).
- **JUnit & Mockito:** For unit and integration testing.

## Configuration

### Environment Variables

Sensitive information such as API keys, database credentials, and JWT secrets are managed using environment variables. In your `application.properties`, reference these variables using the `${VARIABLE_NAME:defaultValue}` syntax:

```properties
# Database configuration
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/bloggingPlatform}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:your_password_here}

# JWT configuration
jwt.secret=${JWT_SECRET:defaultSecret}

# Email configuration
spring.mail.username=${MAIL_USERNAME:your_email@gmail.com}
spring.mail.password=${MAIL_PASSWORD:your_email_password}

# Google Gemini API configuration
google.gemini.api.key=${GOOGLE_GEMINI_API_KEY:your_default_api_key}
google.gemini.api.url=${GOOGLE_GEMINI_API_URL:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}
```

## Running Locally

For local development, create a `.env` file in the root directory (and add it to `.gitignore`) with your sensitive variables:

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/bloggingPlatform  
DB_USERNAME=postgres  
DB_PASSWORD=your_actual_password  
JWT_SECRET=your_actual_jwt_secret  
MAIL_USERNAME=your_email@gmail.com  
MAIL_PASSWORD=your_email_password  
GOOGLE_GEMINI_API_KEY=your_api_key  
GOOGLE_GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent  
```

## API Endpoints

###  **User Endpoints**  
- **POST** `/user/register`: Registers a new user.  
- **POST** `/user/login`: Authenticates a user and issues a JWT.  
- **GET** `/user/myProfile`: Retrieves the profile of the authenticated user.  
- **POST** `/user/update`: Updates the authenticated user's profile.  
- **POST** `/user/sendVerification`: Sends an email verification code.  
- **POST** `/user/verifyEmail`: Verifies the user’s email using a code.  

###  **Post Endpoints**  
- **POST** `/post/create`: Submits a new post creation request.  
- **POST** `/post/update/{postId}`: Submits a post update request.  
- **DELETE** `/post/delete/{postId}`: Submits a post deletion request.  
- **GET** `/post/get/{postId}`: Retrieves details of a specific post.  
- **GET** `/post/getAll`: Retrieves all posts.  
- **GET** `/post/getAllPostsByUserId/{userId}`: Retrieves posts for a specific user.  
- **GET** `/post/search`: Searches posts by title.  

###  **Like Endpoints**  
- **POST** `/like/create`: Likes a post.  
- **DELETE** `/like/delete`: Unlikes a post.  
- **GET** `/like/count/{postUuid}`: Retrieves the like count for a post.  
- **GET** `/like/likedUsers/{postUuid}`: Retrieves the list of usernames that liked a post.  

###  **Admin Endpoints**  
- **GET** `/admin/users`: Retrieves all registered users.  
- **DELETE** `/admin/deleteUser/{userId}`: Deletes a user.  
- **GET** `/admin/pending`: Retrieves all pending post requests.  
- **POST** `/admin/approve/{requestUuid}`: Approves a post request and sends an email notification.  
- **POST** `/admin/reject/{requestUuid}`: Rejects a post request and sends an email notification.  
- **GET** `/admin/dashboard`: Retrieves dashboard statistics (user count, post count, like count).  

###  **AI Endpoints**  
- **POST** `/ai/summary`: Accepts a JSON body with a `"content"` field and returns a 60-character summary of the provided text.  

##  Testing  

The project uses **JUnit** and **Mockito** for unit and integration testing. Tests cover:  

- **User** registration and authentication  
- **Email** verification and profile management  
- **Post** creation, update, deletion, and moderation  
- **Like** functionality  
- **AI** summarization integration  

###  Run tests with:  

```bash
mvn test
```
For integration tests, consider using an in-memory database (e.g., H2) to simulate the production environment.


