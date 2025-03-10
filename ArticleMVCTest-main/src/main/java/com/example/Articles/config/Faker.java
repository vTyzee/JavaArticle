package com.example.Articles.config;

import com.example.Articles.entity.*;
import com.example.Articles.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class Faker implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final com.github.javafaker.Faker faker = new com.github.javafaker.Faker();
    private final Random random = new Random();

    public Faker(UserRepository userRepository, ArticleRepository articleRepository, TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        //seedUsers();
        //seedTags();
        //seedArticles();
    }

    private void seedUsers() {
        for (int i = 0; i < 5; i++) {
            User user = new User();
            System.out.println(faker.internet().emailAddress());
            user.setEmail(faker.internet().emailAddress());
            user.setUsername(faker.name().username());
            user.setImage_url(faker.internet().avatar());
            user.setPassword("password123");
            user.setBio(faker.lorem().sentence());
            userRepository.save(user);
        }
    }

    private void seedTags() {
        for (int i = 0; i < 5; i++) {
            Tag tag = new Tag();
            tag.setName(faker.lorem().word());
            tagRepository.save(tag);
        }
    }

    private void seedArticles() {
        List<User> users = userRepository.findAll();
        List<Tag> tags = tagRepository.findAll();

        if (users.isEmpty() || tags.isEmpty()) {
            return;
        }

        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.setTitle(faker.book().title());
            article.setDescription(faker.lorem().sentence());
            article.setSlug(faker.internet().slug());
            article.setContent(faker.lorem().paragraph());
            article.setAuthor(users.get(random.nextInt(users.size())));
            article.setTags(Collections.singleton(tags.get(random.nextInt(tags.size()))));
            articleRepository.save(article);
        }
    }
}