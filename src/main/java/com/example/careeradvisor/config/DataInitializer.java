package com.example.careeradvisor.config;

import com.example.careeradvisor.entity.CareerRole;
import com.example.careeradvisor.entity.Recommendation;
import com.example.careeradvisor.entity.Skill;
import com.example.careeradvisor.repository.CareerRoleRepository;
import com.example.careeradvisor.repository.RecommendationRepository;
import com.example.careeradvisor.repository.SkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@ConditionalOnProperty(name = "app.seed.data", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final SkillRepository skillRepository;
    private final CareerRoleRepository careerRoleRepository;
    private final RecommendationRepository recommendationRepository;

    public DataInitializer(SkillRepository skillRepository,
                           CareerRoleRepository careerRoleRepository,
                           RecommendationRepository recommendationRepository) {
        this.skillRepository = skillRepository;
        this.careerRoleRepository = careerRoleRepository;
        this.recommendationRepository = recommendationRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (careerRoleRepository.count() > 0) {
            log.info("Database already seeded. Skipping initial data load.");
            return;
        }

        log.info("Seeding initial skills, career roles, and learning recommendations...");

        // 1. Seed Skills (14 core industry skills)
        Map<String, Skill> skills = new HashMap<>();
        List<Skill> skillList = List.of(
            new Skill("Java", "Programming Language"),
            new Skill("Python", "Programming Language"),
            new Skill("SQL", "Database"),
            new Skill("PostgreSQL", "Database"),
            new Skill("Spring Boot", "Framework"),
            new Skill("REST API", "Architecture & Web"),
            new Skill("Data Structures", "Fundamentals"),
            new Skill("Git", "Tools & Version Control"),
            new Skill("Docker", "DevOps & Containers"),
            new Skill("JUnit/Testing", "Testing & QA"),
            new Skill("HTML/CSS", "Frontend Basics"),
            new Skill("Linux Basics", "Operating Systems"),
            new Skill("Microservices Concepts", "Architecture"),
            new Skill("CI/CD Fundamentals", "DevOps")
        );

        for (Skill s : skillList) {
            Skill saved = skillRepository.save(s);
            skills.put(saved.getName(), saved);
        }

        // 2. Seed 4 Career Roles with Required Skills
        // Role 1: Java Backend Developer
        CareerRole javaDev = new CareerRole(
            "Java Backend Developer",
            "Designs, develops, and maintains server-side web applications and RESTful APIs using Java and Spring ecosystem."
        );
        javaDev.setRequiredSkills(new HashSet<>(Set.of(
            skills.get("Java"),
            skills.get("SQL"),
            skills.get("Spring Boot"),
            skills.get("REST API"),
            skills.get("Git"),
            skills.get("JUnit/Testing")
        )));
        careerRoleRepository.save(javaDev);

        // Role 2: Software Engineer
        CareerRole swe = new CareerRole(
            "Software Engineer",
            "Generalist engineering role focusing on algorithmic problem solving, clean code architecture, and software design."
        );
        swe.setRequiredSkills(new HashSet<>(Set.of(
            skills.get("Java"),
            skills.get("Data Structures"),
            skills.get("SQL"),
            skills.get("REST API"),
            skills.get("Git"),
            skills.get("Linux Basics")
        )));
        careerRoleRepository.save(swe);

        // Role 3: QA Automation Engineer
        CareerRole qaEng = new CareerRole(
            "QA Automation Engineer",
            "Builds automated test suites, validates REST API endpoints, and ensures software reliability and quality standards."
        );
        qaEng.setRequiredSkills(new HashSet<>(Set.of(
            skills.get("Java"),
            skills.get("SQL"),
            skills.get("JUnit/Testing"),
            skills.get("REST API"),
            skills.get("Git")
        )));
        careerRoleRepository.save(qaEng);

        // Role 4: Data Analyst
        CareerRole dataAnalyst = new CareerRole(
            "Data Analyst",
            "Collects, inspects, queries, and models structured data to uncover insights and support business decision-making."
        );
        dataAnalyst.setRequiredSkills(new HashSet<>(Set.of(
            skills.get("SQL"),
            skills.get("Python"),
            skills.get("PostgreSQL"),
            skills.get("Git")
        )));
        careerRoleRepository.save(dataAnalyst);

        // 3. Seed Learning Recommendations for Missing Skills
        List<Recommendation> recommendations = List.of(
            new Recommendation(
                skills.get("Spring Boot"),
                "Learn Spring Boot fundamentals, dependency injection (@Autowired, @Component), Spring Data JPA, and build a 3-tier REST API.",
                "Spring Guides (https://spring.io/guides) and Baeldung Spring tutorials."
            ),
            new Recommendation(
                skills.get("Java"),
                "Master core Java fundamentals: OOP principles, Collections framework (List, Map, Set), Exception handling, and Streams API.",
                "Oracle Java Tutorials & Mooc.fi Java Programming."
            ),
            new Recommendation(
                skills.get("SQL"),
                "Practice writing SELECT, JOIN (INNER/LEFT), GROUP BY, HAVING, subqueries, indexing, and aggregate functions.",
                "PostgreSQL Tutorial (https://www.postgresqltutorial.com) & LeetCode Database problems."
            ),
            new Recommendation(
                skills.get("REST API"),
                "Study HTTP methods (GET, POST, PUT, DELETE), status codes (200, 201, 400, 404, 500), JSON request/response formats, and API versioning.",
                "RESTful API Design Best Practices & Mozilla MDN Web Docs."
            ),
            new Recommendation(
                skills.get("Data Structures"),
                "Implement fundamental data structures (Arrays, Linked Lists, Stacks, Queues, Binary Trees, Hash Tables) and analyze Big-O time/space complexity.",
                "NeetCode 150 & GeeksforGeeks Data Structures Roadmap."
            ),
            new Recommendation(
                skills.get("Git"),
                "Practice basic version control workflows: git init, clone, add, commit, push, pull, branch, merge, and handling merge conflicts.",
                "Pro Git Book (https://git-scm.com/book) & GitHub Skills interactive guides."
            ),
            new Recommendation(
                skills.get("JUnit/Testing"),
                "Learn unit testing with JUnit 5 annotations (@Test, @BeforeEach), assertions (assertEquals, assertThrows), and mocking dependencies with Mockito.",
                "JUnit 5 User Guide & Baeldung Mockito Guide."
            ),
            new Recommendation(
                skills.get("Linux Basics"),
                "Familiarize yourself with essential terminal commands (ls, cd, grep, find, chmod, ps, curl), file permissions, and shell scripting.",
                "Linux Journey (https://linuxjourney.com)."
            ),
            new Recommendation(
                skills.get("Python"),
                "Learn Python syntax, data types (lists, dicts, tuples), list comprehensions, and data analysis packages.",
                "Official Python Tutorial & Kaggle Python Course."
            ),
            new Recommendation(
                skills.get("PostgreSQL"),
                "Understand PostgreSQL schema creation, data constraints, ACID properties, foreign keys, and indexes.",
                "Official PostgreSQL Documentation."
            )
        );

        recommendationRepository.saveAll(recommendations);

        log.info("Successfully seeded 14 skills, 4 career roles, and {} learning recommendations.", recommendations.size());
    }
}
