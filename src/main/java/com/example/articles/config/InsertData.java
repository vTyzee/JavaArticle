package com.example.articles.config;

import com.example.articles.entities.Article;
import com.example.articles.entities.ArticleComment;
import com.example.articles.entities.ArticleFavorite;
import com.example.articles.entities.Author;
import com.example.articles.entities.Tag;
import com.example.articles.entities.User;
import com.example.articles.repositories.ArticleCommentRepository;
import com.example.articles.repositories.ArticleFavoriteRepository;
import com.example.articles.repositories.ArticleRepository;
import com.example.articles.repositories.AuthorRepository;
import com.example.articles.repositories.TagRepository;
import com.example.articles.repositories.UserRepository;
import com.github.javafaker.Faker;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class InsertData {

    private final UserRepository userRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleFavoriteRepository articleFavoriteRepository;

    public InsertData(UserRepository userRepository,
                      AuthorRepository authorRepository,
                      TagRepository tagRepository,
                      ArticleRepository articleRepository,
                      ArticleCommentRepository articleCommentRepository,
                      ArticleFavoriteRepository articleFavoriteRepository) {
        this.userRepository = userRepository;
        this.authorRepository = authorRepository;
        this.tagRepository = tagRepository;
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.articleFavoriteRepository = articleFavoriteRepository;
    }

    @PostConstruct
    public void init() {
        Faker faker = new Faker(Locale.forLanguageTag("en"));

        User adminUser = userRepository.findByUsername("admin");
        if (adminUser == null) {
            adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@example.com");
            adminUser.setRole(User.Roles.ADMIN_ROLE);
            adminUser.setCreatedAt(LocalDateTime.now().withSecond(0).withNano(0));
            adminUser.setBio("Администратор системы");
            adminUser.setImageUrl("https://via.placeholder.com/150");
        }
        adminUser.setPassword("123");
        userRepository.save(adminUser);

        List<Author> authors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Author author = new Author();
            author.setName(faker.name().fullName());
            author.setBio(faker.lorem().sentence());
            authors.add(author);
        }
        authors = authorRepository.saveAll(authors);

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)).withSecond(0).withNano(0));
            user.setEmail(faker.internet().emailAddress());
            user.setUsername(faker.name().username());
            user.setImageUrl(faker.internet().avatar());
            user.setPassword(faker.internet().password());
            user.setBio(faker.lorem().sentence());
            users.add(user);
        }
        users = userRepository.saveAll(users);

        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Tag tag = new Tag();
            tag.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)).withSecond(0).withNano(0));
            tag.setName(faker.book().genre());
            tags.add(tag);
        }
        tags = tagRepository.saveAll(tags);

        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Article article = new Article();
            article.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)).withSecond(0).withNano(0));
            article.setUpdatedAt(LocalDateTime.now().withSecond(0).withNano(0));
            article.setDescription(faker.lorem().sentence());
            article.setSlug(faker.internet().slug());
            article.setTitle(faker.book().title());
            article.setBody(faker.lorem().paragraph(3));
            Author randomAuthor = authors.get(faker.number().numberBetween(0, authors.size()));
            article.setAuthor(randomAuthor);
            Set<Tag> articleTags = new HashSet<>();
            int numTags = faker.number().numberBetween(1, 3);
            for (int j = 0; j < numTags; j++) {
                Tag randomTag = tags.get(faker.number().numberBetween(0, tags.size()));
                articleTags.add(randomTag);
            }
            article.setTags(articleTags);
            User randomOwner = users.get(faker.number().numberBetween(0, users.size()));
            article.setOwner(randomOwner);
            articles.add(article);
        }
        articles = articleRepository.saveAll(articles);

        List<ArticleComment> comments = new ArrayList<>();
        for (Article article : articles) {
            int commentCount = faker.number().numberBetween(1, 6);
            for (int i = 0; i < commentCount; i++) {
                ArticleComment comment = new ArticleComment();
                comment.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)).withSecond(0).withNano(0));
                comment.setUpdatedAt(LocalDateTime.now().withSecond(0).withNano(0));
                comment.setBody(faker.lorem().sentence());
                comment.setArticle(article);
                User randomUser = users.get(faker.number().numberBetween(0, users.size()));
                comment.setUser(randomUser);
                comments.add(comment);
            }
        }
        articleCommentRepository.saveAll(comments);

        List<ArticleFavorite> favorites = new ArrayList<>();
        for (Article article : articles) {
            int favCount = faker.number().numberBetween(0, 4);
            for (int i = 0; i < favCount; i++) {
                ArticleFavorite favorite = new ArticleFavorite();
                favorite.setCreatedAt(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)).withSecond(0).withNano(0));
                favorite.setUpdatedAt(LocalDateTime.now().withSecond(0).withNano(0));
                favorite.setArticle(article);
                User randomUser = users.get(faker.number().numberBetween(0, users.size()));
                favorite.setUser(randomUser);
                favorites.add(favorite);
            }
        }
        articleFavoriteRepository.saveAll(favorites);
    }
}
