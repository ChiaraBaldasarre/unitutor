# Unitutor Project – README

## 1. Short description of the project
Unitutor is a system designed to simplify the management of tutoring sessions for both students and professors. Users log in using their DNI, and the system automatically identifies their role so the correct permissions and views are assigned. The platform includes tutoring search tools, session history, and features for managing or creating new tutoring sessions.

---

## 2. Main functionalities
- Login using DNI with automatic role recognition.
- Search for tutoring sessions using different filters such as:
  - Subject
  - Modality (Group or 1:1)
  - Available schedules
  - Combined filters
- View a complete history of past tutoring sessions.
- Cancel tutoring registrations within the allowed time frame.
- Tutors can create new sessions by entering subject, schedule, modality, and available spots.
- Access is restricted based on the user’s role.
- Users are redirected to their specific dashboard according to their roles.

---

## 3. Technical details
Along the project the following tools are being use:

- IntelliJ Idea Ultimate
- Git
- Git Lab

Programming Language: Java V.17
Data Base: MySQL

---

## 4. Project requirements

### 4.1 Functional requirements
- Role authentication with DNI only.
- Session management. Menu option such as 0. Exit closes the session and returns to the DNI prompt
- Tutoring search and filter. Interactive console menu asks for subject, time and modality and shows the results in the console
- History view.
- Cancellation management.
- Tutoring creation for teachers. Console form asks for subject, time, modality and capacity and creates the tutoring session
- Notifications. Shows confirmation messages for booking and cancellation in the console only.
### 4.2. Non-Funtional requirements
- Authentication security. System validates DNI input
- Authorization control. Menus and options are restricted according to the user role student or teacher
- Privacy. Console output does not show sensitive information such as teacher DNI in any list
---

## 5. Steps to execute the project
1. Clone the repository from GitLab.
2. Review the backlog to identify the tasks currently in progress.
3. Test the functionalities described in the user stories:
   - Tutoring search
   - Session cancellation
   - Tutoring creation
   - Viewing the tutoring history
4. Update documentation as progress is made.
5. Commit and push changes following the team’s conventions.

---

## 7. Team members
- **Lilian Laime** – Team Lead  
- **Lucas Martinez** – Scrum Master  
- **Chiara Baldasarre** – Development & QA  
- **Alessandro Adrian Ruiz** – Development & QA  

---

## Project repository
https://gitlab.com/jala-university1/cohort-6/ES.CSSD-113.GA.T2.25.M2/SD/grupo3-unitutorcapstone

-