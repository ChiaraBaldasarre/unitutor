# Unitutor Project – README

## 1. Short description of the project

Unitutor is a system designed to simplify the management of tutoring sessions for both students and professors. Users log in using their DNI, and the system automatically identifies their role so the correct permissions and views are assigned. The platform includes tutoring search tools, session history, and features for managing or creating new tutoring sessions.

---

## 2. Main functionalities (high level)

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

## 3. Architecture overview

- **Pattern:** Layered MVC (console-based UI)
  - **Controller layer:** `ApplicationController` (root menu), `AuthenticationController`, `StudentSessionController`, `ProfessorSessionController`, `EnrollmentCancellationController`.
  - **Service layer:** business logic in `UserService`, `TutoringSessionService`, `StudentProgressService`, `ProfessorFormService`, `EnrollmentCancellationService`, `AuthorizationService`.
  - **Repository layer:** data access with Spring Data JPA (`UserRepository`, `TutoringSessionRepository`, `EnrollmentRepository`, `RolRepository`).
  - **Domain/model:** entities `User`, `Role`, `TutoringSession`, `Enrollment`, `SessionHistory` plus utilities like `DniValidator`.
  - **View (console):** `ConsoleIO`, `StudentMenuView`, `ProfessorMenuView`, `RoleMenuView` for text interaction.
- **Main flow:**
  1. User enters DNI → `AuthenticationController` validates and resolves role.
  2. `ApplicationController` shows the role-based menu (STUDENT/PROFESSOR).
  3. Student can search sessions, view history, and cancel enrollments (>24h rule).
  4. Professor can create sessions, manage active sessions, and upload grades.
  5. All operations go through services; persistence via JPA repositories to MySQL.

---

## 4. Technical details

Tools used in the project:

- IntelliJ Idea Ultimate
- Git
- Git Lab

Programming Language: Java 17
Database: MySQL

---

## 5. Project requirements

### 5.1 Functional requirements

- Role authentication with DNI only.
- Session management. Menu option such as 0. Exit closes the session and returns to the DNI prompt
- Tutoring search and filter. Interactive console menu asks for subject, time and modality and shows the results in the console
- History view.
- Cancellation management.
- Tutoring creation for teachers. Console form asks for subject, time, modality and capacity and creates the tutoring session
- Notifications. Shows confirmation messages for booking and cancellation in the console only.

### 5.2. Non-Functional requirements

- Authentication security. System validates DNI input
- Authorization control. Menus and options are restricted according to the user role student or teacher
- Privacy. Console output does not show sensitive information such as teacher DNI in any list

---

## 6. Steps to execute the project

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

## 7. How to use the program (step by step)

1. **Prerequisites:** JDK 17 installed. Maven is not required separately; the wrapper (`mvnw.cmd`) is included.
2. **Configuration**
  Configure your `src/main/resources/application.properties` file with the following settings to optimize logging and SQL output:
  spring.jpa.show-sql=false
  logging.console.enabled=false
  spring.jpa.properties.hibernate.format_sql=false
  logging.level.org.hibernate.SQL=WARN
  logging.level.org.hibernate.type.descriptor.sql=WARN
3. **Build and run:** In PowerShell inside the project directory, run: `./mvnw.cmd spring-boot:run`. Wait until the app starts without errors.
4. **Login:** In the console, enter your DNI (8 digits). The system auto-detects whether you are STUDENT or PROFESSOR.
5. **Main menu:**

- STUDENT: options to search tutoring sessions, view history, and cancel enrollments.
- PROFESSOR: options to create sessions, manage active sessions, and upload grades.

5. **Search tutoring sessions (student):**

- Choose a filter by subject, date, or modality.
- Enter the requested value and review the results listed in the console.

6. **Cancel an enrollment (student):**

- Choose “Cancel enrollment,” enter the session ID; only possible if more than 24 hours remain.

7. **Create a tutoring session (professor):**

- Enter subject, date/time, duration, capacity, and modality. Confirm to save.

8. **Upload grades (professor):**

- Select an active session and follow the prompts to upload grades.

9. **Exit:** Use option `0` in any menu to sign out; this returns to the DNI prompt.

---

## 8. Team members

- **Lilian Laime** – Team Lead
- **Lucas Martinez** – Scrum Master
- **Chiara Baldasarre** – Development & QA
- **Alessandro Adrian Ruiz** – Development & QA

---

## Project repository

https://gitlab.com/jala-university1/cohort-6/ES.CSSD-113.GA.T2.25.M2/SD/grupo3-unitutorcapstone
