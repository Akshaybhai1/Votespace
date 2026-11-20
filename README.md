# VoteSpace – Online Voting System
### README + Presentation Guide (explained from zero)

> Read this file from top to bottom once. After that you will be able to **explain the project, show the demo, explain the code, and answer viva questions.**

---

## PART 1 – What is this project? (30-second explanation)

> "VoteSpace is an **online voting web application** built with **Spring Boot**. An **Admin** registers voters and candidates and sets the election date and time. Each user gets their **login details by email**. **Voters** log in and can vote **only once**. **Candidates** have their own dashboard. Everyone can see a **live graph** of the results."

### The three types of users

| User | What they can do |
|---|---|
| **Admin** | Add / edit / delete voters and candidates (with photo), set election start & end time, reset passwords, see live results, reset votes |
| **Candidate** | See own votes, rank and share, edit own manifesto/photo, see the live graph |
| **Voter** | See candidates, vote once (only when election is live), see the live graph |

### Features checklist (say these in your presentation)

- Login system with **Spring Security** (nobody can open any page without logging in)
- **Separate dashboard** for Admin, Candidate and Voter
- Admin **CRUD** (Create, Read, Update, Delete) for voters and candidates
- **Photo upload** for voters and candidates
- **Email** sent automatically when a voter/candidate is registered
- Admin sets **election date and time**; voting works only inside that time
- **One person = one vote** (cannot vote twice)
- **Live graph** (bar chart + doughnut chart) for everyone
- Attractive, mobile-friendly design

---

## PART 2 – Technologies (what each one does, in simple words)

| Technology | Simple meaning |
|---|---|
| **Java 17** | The programming language |
| **Spring Boot** | A framework that makes building web apps easy; it starts the server for you |
| **Spring MVC** | Receives the browser's request and sends back a page |
| **Spring Security** | The "security guard": login, logout, roles, password protection |
| **Spring Data JPA / Hibernate** | Saves and reads data from the database *without writing SQL* |
| **MySQL** | The database where all data is stored |
| **JSP** | The HTML pages that can show data from Java |
| **Bootstrap** | Makes the pages look good and work on mobile |
| **Chart.js** | Draws the graphs |
| **JavaMail** | Sends emails |
| **Maven (`pom.xml`)** | Downloads the libraries the project needs |

---

## PART 3 – How the project works (the big idea)

Every action in the app follows the same path:

```
 BROWSER  ──►  SECURITY  ──►  CONTROLLER  ──►  SERVICE  ──►  REPOSITORY  ──►  MYSQL
 (user)      (guard)        (receptionist)   (brain)       (database helper)   (storage)
                                  │
                                  ▼
                             JSP PAGE  (what the user sees)
```

Think of it like a **restaurant**:

| Restaurant | Project |
|---|---|
| Door guard checks your ticket | **Spring Security** checks login & role |
| Waiter takes your order | **Controller** receives the request |
| Chef follows the rules and cooks | **Service** applies the rules (e.g. "no double voting") |
| Storeroom | **Repository + MySQL** |
| The plate served to you | **JSP page** |

### Example: a voter clicks "Vote"

1. Browser sends `POST /voter/vote`.
2. **Security** checks: logged in? role = VOTER? secret CSRF token valid?
3. **VoterController** receives it and calls `VoteService.castVote()`.
4. **VoteService** checks the rules in this order:
   1. Is this user really a voter?
   2. Is the election **LIVE** right now?
   3. Has this voter **already voted**?
   4. Does the candidate exist?
5. If everything is OK → the vote is saved in the `votes` table.
6. The voter is redirected to the dashboard and sees **"Your vote is recorded"**.

---

## PART 4 – Project structure (where is what)

```
VoteSpace
├── pom.xml                       → list of libraries (Maven)
├── README.md
└── src/main
    ├── java/com/universal
    │   ├── VotingPoolApp1111Application.java   → START button of the project
    │   ├── config/        → security + startup settings
    │   ├── controller/    → receives requests, opens pages
    │   ├── api/           → gives results as JSON for the graphs
    │   ├── service/       → ALL business rules (brain of the project)
    │   ├── repository/    → talks to database
    │   ├── entities/      → Java classes = database tables
    │   ├── dto/           → small objects to carry data
    │   └── exception/     → friendly error messages
    ├── resources
    │   ├── application.properties   → settings (port, database, mail)
    │   └── static/  css/ js/ images/   → design, scripts, default avatar
    └── webapp/WEB-INF/views         → all JSP pages
        ├── layout/  (header, footer)
        ├── admin/   (dashboard, voters, candidates, election)
        ├── voter/   candidate/
        └── index.jsp  login.jsp  results.jsp  error.jsp
```

**Rule to remember:** `entities` = tables, `repository` = database access, `service` = rules, `controller` = pages.

---

## PART 5 – The database (4 tables)

| Table | Stores |
|---|---|
| `app_users` | Everyone who can log in: name, email, **hashed** password, role, phone, photo |
| `candidates` | Candidate extra info: party, manifesto (linked to `app_users`) |
| `votes` | Who voted for whom and when |
| `election` | One row: title, start time, end time |

```
app_users ◄──── candidates          app_users ◄──── votes ────► candidates
(id, email,     (user_id, party,    (voter_id UNIQUE, candidate_id, time)
 password, role) manifesto)
```

**Important:** `votes.voter_id` is **UNIQUE** → the database itself refuses a second vote from the same person.

Tables are created **automatically** (`ddl-auto=update`), you never write `CREATE TABLE`.

---

## PART 6 – The code explained (file by file, simple language)

### 6.1 `VotingPoolApp1111Application.java` – the start button
```java
@SpringBootApplication      // "Spring, scan everything and start the app"
@EnableAsync                // allow background tasks (bulk emails)
public class VotingPoolApp1111Application {
    public static void main(String[] args) {
        SpringApplication.run(VotingPoolApp1111Application.class, args);
    }
}
```

### 6.2 Entities – Java classes that become tables

**`AppUser`** → table `app_users`
```java
@Entity @Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue private Long id;        // auto number
    private String fullName;
    @Column(unique = true) private String email; // email is the username
    private String password;                    // stored as BCrypt hash, never plain text
    @Enumerated(EnumType.STRING) private Role role;  // ADMIN / CANDIDATE / VOTER
    private String phone;
    private String photo;                       // file name of uploaded photo
}
```
**`Vote`** → table `votes` (the key rule):
```java
@Table(name = "votes",
       uniqueConstraints = @UniqueConstraint(columnNames = "voter_id"))
```
→ Same voter cannot appear twice. **This is your safety net against double voting.**

**`Election`** → one row with `title`, `startTime`, `endTime`.
**`ElectionStatus`** → `NOT_SCHEDULED`, `UPCOMING`, `LIVE`, `ENDED`.

### 6.3 Repositories – database without SQL
```java
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);   // Spring writes: SELECT ... WHERE email = ?
    boolean existsByEmail(String email);
}
```
You only write the **method name**; Spring creates the SQL. That is the magic of Spring Data JPA.

### 6.4 `VoteService` – the most important file
```java
@Transactional
public Vote castVote(String voterEmail, Long candidateId) {
    AppUser voter = users.findByEmail(voterEmail).orElseThrow(...);

    if (voter.getRole() != Role.VOTER)
        throw new AppException("Only registered voters can cast a vote.");

    if (elections.status(elections.get()) != ElectionStatus.LIVE)
        throw new AppException("Voting is not open right now.");

    if (votes.existsByVoterId(voter.getId()))
        throw new AppException("You have already voted.");

    Candidate candidate = candidates.findById(candidateId).orElseThrow(...);

    try {
        return votes.saveAndFlush(new Vote(voter, candidate));
    } catch (DataIntegrityViolationException ex) {
        throw new AppException("You have already voted.");   // 2 clicks at same time
    }
}
```
**Say in presentation:** "Double voting is blocked in **three layers**: the page hides the button, the service checks, and the database has a UNIQUE rule."

### 6.5 `ElectionService` – how "LIVE" is decided
```java
if (start == null || end == null) return NOT_SCHEDULED;
if (now.isBefore(start))          return UPCOMING;
if (now.isBefore(end))            return LIVE;
return ENDED;
```
No timer needed: the status is calculated from the clock every time.

### 6.6 `UserManagementService` – admin registers a voter
Steps inside `registerVoter(...)`:
1. Validate name, email, phone.
2. Check email is not already used.
3. Save the photo (`FileStorageService`).
4. Generate a **random 10-character password**.
5. **Hash it with BCrypt** and save the user.
6. Send an email with the login details (`MailService`).

If email fails, the user is still created and the admin sees the password on screen.

### 6.7 `FileStorageService` – photo upload
- Accepts only real images (checks the file's first bytes: JPG / PNG / GIF / WEBP).
- Max 5 MB.
- Saves in the `uploads/` folder with a **random name** (so nobody can overwrite files).

### 6.8 `MailService` – sending email
Builds an HTML email with the username, password and election schedule, and sends it with `JavaMailSender` (Gmail SMTP). It never crashes the app if sending fails.

### 6.9 Security classes (in `config/`)

**`SecurityConfig`** – who can open what:
```java
.dispatcherTypeMatchers(FORWARD, ERROR, INCLUDE).permitAll()      // needed for JSP (fixes redirect loop)
.requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()  // public
.requestMatchers("/admin/**").hasRole("ADMIN")
.requestMatchers("/candidate/**").hasRole("CANDIDATE")
.requestMatchers("/voter/**").hasRole("VOTER")
.anyRequest().authenticated()                                      // everything else = login needed
```
- `PasswordEncoder` = **BCrypt** (one-way hash, passwords can't be read back).
- CSRF protection is ON (every form carries a secret token).

**`AppUserDetailsService`** – "when someone logs in, find the user in MySQL by email."

**`RoleBasedSuccessHandler`** – after login: ADMIN → `/admin/dashboard`, CANDIDATE → `/candidate/dashboard`, VOTER → `/voter/dashboard`.

**`DataInitializer`** – on first start creates the default **admin** and the election row.

**`WebConfig`** – makes the `uploads/` folder visible in the browser at `/uploads/...` so photos show.

### 6.10 Controllers – which URL does what

| Controller | URLs | Job |
|---|---|---|
| `HomeController` | `/`, `/login`, `/dashboard` | landing page, login page, send user to own dashboard |
| `AdminController` | `/admin/dashboard`, `/admin/election` | stats, set election time, reset votes |
| `AdminVoterController` | `/admin/voters/...` | voter CRUD |
| `AdminCandidateController` | `/admin/candidates/...` | candidate CRUD |
| `VoterController` | `/voter/dashboard`, `/voter/vote` | ballot + vote |
| `CandidateController` | `/candidate/dashboard`, `/candidate/profile` | candidate stats + edit profile |
| `ResultsController` | `/results` | graph page for everyone |
| `ResultsApiController` | `/api/results` | JSON data for live graph |

### 6.11 Frontend files

| File | Job |
|---|---|
| `header.jsp` / `footer.jsp` | shared top (sidebar menu depends on role) and bottom of every page |
| `*.jsp` pages | the screens; `${...}` shows data, `<c:forEach>` loops, `<c:if>` conditions |
| `style.css` | the whole look: colours, cards, sidebar, mobile design |
| `app.js` | countdown timer, delete confirmation, photo preview, search box, vote popup |
| `results.js` | every 10 seconds calls `/api/results` and redraws the charts (Chart.js) |

---

## PART 7 – Live demo script (follow in this order)

Open: **http://localhost:8080** (your port in `application.properties`)

1. **Show landing page** → "This is the public page with election info and countdown."
2. **Login as Admin** (email + password from `application.properties`, default `Admin@123`).
3. **Admin dashboard** → point at stats cards, graph, election card.
4. **Candidates → Add candidate** → fill name, email, party, manifesto, upload photo → Save.
   Say: *"The candidate automatically receives an email with login details."*
5. **Voters → Register voter** → add 2–3 voters with photos.
6. **Election schedule** → set start = now, end = 1 hour later → Save.
7. **Open a second browser / Incognito** → login as a **voter** (use the password from the email, or the one shown on the admin screen if email isn't configured).
8. **Voter dashboard** → click **Vote** → confirm in popup → "Your vote is recorded."
9. Try to vote again → **buttons are gone** (and the server would reject it anyway).
10. **Open Live results** → show the bar and doughnut chart updating.
11. **Login as candidate** → show votes, rank, share and the profile edit form.
12. **Back to admin** → Voters list shows ✔ *Voted*; show Edit, Reset password, Delete.
13. **Try opening `/admin/dashboard` while logged in as voter** → shows 403 → "Role-based security works."

---

## PART 8 – 2-minute presentation script

> "Good morning. My project is **VoteSpace**, an online voting system built with Spring Boot, Spring Security, MySQL and JSP.
>
> There are three roles. The **admin** registers voters and candidates, uploads their photos, and sets the election start and end time. When someone is registered, the system **emails** them their login details.
>
> **Voters** log in and see a ballot. They can vote **only once** and only while the election is live. I made sure of that in three ways: the page hides the vote button, the service layer checks, and the database has a **unique constraint** on the voter.
>
> **Candidates** have their own dashboard with votes, rank and share, and can edit their manifesto.
>
> **Everyone** can see a live graph of the results, built with Chart.js, which refreshes every 10 seconds.
>
> For security, I used **Spring Security** with **BCrypt** password hashing, **role-based access** and **CSRF protection**. The project follows the layered architecture: **Controller, Service, Repository, Entity**. Thank you."

---

## PART 9 – Viva questions and answers

**Q1. What is Spring Boot?**
A framework that makes it easy to build Java web applications with automatic configuration and an embedded server.

**Q2. What is MVC?**
Model–View–Controller. *Model* = data, *View* = JSP pages, *Controller* = receives requests and chooses the page.

**Q3. Why did you use layers (controller / service / repository)?**
To keep code clean: controller handles requests, service holds rules, repository handles database. Easy to maintain and test.

**Q4. How do you prevent double voting?**
Three layers: (1) UI hides the button after voting, (2) `VoteService` checks `existsByVoterId`, (3) UNIQUE constraint on `voter_id` in the `votes` table.

**Q5. How are passwords stored?**
Hashed with **BCrypt**, a one-way algorithm. Even the admin cannot read a password from the database.

**Q6. How does login work?**
User enters email + password → `AppUserDetailsService` loads the user from MySQL → Spring Security compares the typed password with the stored hash → if correct, a session is created and the user is redirected to the dashboard of their role.

**Q7. What is role-based access?**
Each URL group is locked to a role (`/admin/**` → ADMIN etc.). A voter opening an admin URL gets 403 Forbidden.

**Q8. What is CSRF?**
An attack where another website tricks a logged-in user into sending a request. Spring Security blocks it by requiring a secret token in every form.

**Q9. What is JPA / Hibernate?**
It maps Java classes to database tables, so we save objects instead of writing SQL.

**Q10. What does `ddl-auto=update` do?**
Creates or updates the tables automatically from the entity classes.

**Q11. How does the election time work?**
Admin saves start/end. `ElectionService.status()` compares the current time with them and returns UPCOMING, LIVE or ENDED. Voting is allowed only when LIVE.

**Q12. How is the email sent?**
Spring Mail (`JavaMailSender`) through Gmail SMTP using an App Password.

**Q13. How are photos stored?**
On disk in the `uploads/` folder with a random name; only the file name is saved in the database. Only real images up to 5 MB are accepted.

**Q14. How does the live graph work?**
`results.js` calls `/api/results` every 10 seconds, receives JSON, and updates the Chart.js charts without reloading the page.

**Q15. What is a DTO?**
A simple object used only to carry data (for example `ResultsSummary` for the graph).

**Q16. What was the redirect-loop problem and how did you fix it?**
Spring Security 6 also checks the internal *forward* used to show a JSP page. Not-logged-in users were redirected to `/login` again and again. I fixed it by allowing `FORWARD`, `ERROR` and `INCLUDE` dispatch types in `SecurityConfig`.

**Q17. What is `@Transactional`?**
It makes a method run as one database transaction: everything succeeds together or is rolled back.

**Q18. What is `Optional`?**
A Java box that may or may not contain a value, used to avoid null errors (`findByEmail` returns an `Optional`).

**Q19. What can be improved in future?**
Change-password page, OTP verification, multiple elections/positions, export results to PDF/Excel, voter self-registration with admin approval.

---

## PART 10 – How to run the project

1. Install **Java 17+**, **MySQL**, and **Eclipse/STS**.
2. Start MySQL. Check the username/password in `src/main/resources/application.properties`.
3. (Optional, for emails) create a Google **App Password** and keep it in a `secrets.properties` file or environment variables – **never** type it into a file you upload to GitHub.
4. In Eclipse: **Import → Existing Maven Project** → select the project.
5. Run `VotingPoolApp1111Application` → **Run As → Spring Boot App**.
6. Open **http://localhost:8080** (or the port in your properties file).
7. Log in as admin with the email/password from these lines:
   ```properties
   app.admin.email=...
   app.admin.password=...
   ```
   (The admin is created only the first time the database is empty.)

---

## PART 11 – Common problems and quick fixes

| Problem | Fix |
|---|---|
| `ERR_TOO_MANY_REDIRECTS` | Make sure `SecurityConfig` has the `dispatcherTypeMatchers(FORWARD, ERROR, INCLUDE).permitAll()` line, clean the project, try Incognito |
| `Access denied for user 'root'` | Wrong MySQL password in `application.properties` |
| `Communications link failure` | MySQL is not running |
| Port already in use | Stop the old run or change `server.port` |
| Cannot log in as admin | Admin exists from an earlier run; check `SELECT email, role FROM app_users;` or drop the database and restart |
| Emails not coming | Use a Gmail **App Password**; the app still works and shows the password to the admin |
| Photo not visible | Check that the `uploads/` folder exists where the app starts |
| `app.admin.name is an unknown property` | Only an Eclipse warning, ignore it |
| Voter cannot vote | Election must have start/end set and current time must be between them |

---

## PART 12 – Security notes (mention these – they impress examiners)

- Passwords are **BCrypt-hashed**.
- **CSRF token** in every form.
- **Role-based** URL protection.
- All user text is printed with `<c:out>` / `escapeXml` → protects against **XSS**.
- Uploads: real-file-type check, size limit, random names, stored outside the web folder.
- Secrets (database / Gmail password) must be kept in **environment variables or `secrets.properties`**, never in Git.

---

### One-line summary to remember
**"Browser → Spring Security → Controller → Service (rules) → Repository → MySQL, and the answer comes back as a JSP page. Double voting is blocked by the UI, the service and a UNIQUE database rule."**
