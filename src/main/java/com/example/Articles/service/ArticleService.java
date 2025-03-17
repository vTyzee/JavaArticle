package com.example.Articles.service;

import com.example.Articles.entity.Article;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArticleService {
    List<Article> getAllArticles();
    Optional<Article> getArticleById(Long id);
    Article createArticle(Article article);
    Article updateArticle(Long id, Article updatedArticle);
    void deleteArticle(Long id);
    List<Article> getArticlesByAuthor(UUID authorId);
    List<Article> getArticlesByTag(Long tagId);
    List<Article> searchArticles(String query);

    List<Article> getArticlesByAuthor(Long authorId);
}
