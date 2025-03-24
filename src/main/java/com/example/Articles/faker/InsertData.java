package com.example.Articles.faker;

import com.example.Articles.entity.Article;
import com.example.Articles.entity.Author;
import com.example.Articles.entity.Tag;
import com.example.Articles.entity.User;
import com.example.Articles.repository.ArticleRepository;
import com.example.Articles.repository.AuthorRepository;
import com.example.Articles.repository.TagRepository;
import com.example.Articles.repository.UserRepository;
import com.example.Articles.roles.Role;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class InsertData implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final AuthorRepository authorRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final Faker faker = new Faker();
    private final Random random = new Random();

    public InsertData(
            UserRepository userRepository,
            ArticleRepository articleRepository,
            TagRepository tagRepository,
            AuthorRepository authorRepository,
            BCryptPasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.tagRepository = tagRepository;
        this.authorRepository = authorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedAuthors();   // 1) Генерируем авторов
        seedUsers();     // 2) Генерируем пользователей (с хешированием паролей!)
        seedTags();      // 3) Генерируем теги
        seedArticles();  // 4) Генерируем статьи
    }

    /**
     * Создание нескольких Авторов (если их нет).
     */
    private void seedAuthors() {
        if (authorRepository.count() > 0) {
            return; // не дублируем
        }
        for (int i = 0; i < 5; i++) {
            Author author = new Author();
            author.setName(faker.name().fullName());
            author.setBio(faker.lorem().sentence());
            authorRepository.save(author);
        }
        System.out.println("Авторы успешно добавлены!");
    }

    /**
     * Создание нескольких пользователей + 1 админа
     * ВАЖНО: пароль обязательно хешируем!
     */
    private void seedUsers() {
        if (userRepository.count() > 0) {
            return; // уже созданы
        }

        // Создадим 1 ADMIN пользователя
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@example.com");
        // Важно: хешируем пароль "admin123"
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setRole(Role.ADMIN);  // ставим роль ADMIN
        userRepository.save(adminUser);

        // Создадим несколько обычных пользователей
        for (int i = 0; i < 5; i++) {
            User user = new User();
            user.setUsername(faker.name().username());
            user.setEmail(faker.internet().emailAddress());
            // Тут тоже хешируем пароль "password123"
            user.setPassword(passwordEncoder.encode("password123"));
            // Роль по умолчанию: USER (или можно явно user.setRole(Role.USER))
            userRepository.save(user);
        }
        System.out.println("Пользователи успешно добавлены!");
    }

    /**
     * Создание 5 случайных тегов (если их нет)
     */
    private void seedTags() {
        if (tagRepository.count() > 0) {
            return;
        }
        for (int i = 0; i < 5; i++) {
            Tag tag = new Tag();
            tag.setName(faker.lorem().word());
            tagRepository.save(tag);
        }
        System.out.println("Теги успешно добавлены!");
    }

    /**
     * Генерация статей, каждая привязана к одному случайному Автору и одному случайному Тегу.
     */
    private void seedArticles() {
        if (articleRepository.count() > 0) {
            return;
        }
        List<Author> authors = authorRepository.findAll();
        List<Tag> tags = tagRepository.findAll();
        if (authors.isEmpty() || tags.isEmpty()) {
            System.out.println("Нет авторов или тегов, не можем создать статьи.");
            return;
        }

        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.setTitle(faker.book().title());
            article.setDescription(faker.lorem().sentence());
            article.setSlug(faker.lorem().word());
            article.setContent(faker.lorem().paragraph(3));

            // Случайный автор
            Author randomAuthor = authors.get(random.nextInt(authors.size()));
            article.setAuthor(randomAuthor);

            // Случайный тег (или можно несколько)
            Tag randomTag = tags.get(random.nextInt(tags.size()));
            article.setTags(Collections.singleton(randomTag));

            articleRepository.save(article);
        }
        System.out.println("Статьи успешно добавлены!");
    }

}
