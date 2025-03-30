package com.example.articles.service;

import com.example.articles.entities.Article;
import com.example.articles.repositories.ArticleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // Получаем статьи, отсортированные по updatedAt (новые/отредактированные первыми)
    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
    }

    @Override
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    @Override
    public Article createArticle(Article article) {
        LocalDateTime now = LocalDateTime.now();
        article.setCreatedAt(now);
        article.setUpdatedAt(now);
        return articleRepository.save(article);
    }

    @Override
    public Article updateArticle(Long id, Article updatedArticle) {
        return articleRepository.findById(id)
                .map(article -> {
                    article.setTitle(updatedArticle.getTitle());
                    article.setDescription(updatedArticle.getDescription());
                    article.setSlug(updatedArticle.getSlug());
                    article.setBody(updatedArticle.getBody());
                    // Обновляем updatedAt при редактировании
                    article.setUpdatedAt(LocalDateTime.now());
                    article.setAuthor(updatedArticle.getAuthor());
                    article.setTags(updatedArticle.getTags());
                    return articleRepository.save(article);
                })
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public List<Article> getArticlesByAuthor(Long authorId) {
        return articleRepository.findByAuthorId(authorId);
    }

    @Override
    public List<Article> getArticlesByTag(Long tagId) {
        return articleRepository.findByTagId(tagId);
    }

    @Override
    public List<Article> searchArticles(String query) {
        return articleRepository.searchArticles(query);
    }

    @Override
    public List<Article> getArticlesByCreatedMonth(int month) {
        return articleRepository.findByCreatedMonth(month);
    }
}
