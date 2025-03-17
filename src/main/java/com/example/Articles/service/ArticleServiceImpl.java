package com.example.Articles.service;

import com.example.Articles.entity.Article;
import com.example.Articles.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public Optional<Article> getArticleById(Long id) {
        return articleRepository.findById(id);
    }

    @Override
    public Article createArticle(Article article) {
        return articleRepository.save(article);
    }
    @Override
    public List<Article> searchArticles(String query) {

        return null; // Replace with actual implementation
    }

    @Override
    public Article updateArticle(Long id, Article updatedArticle) {
        return articleRepository.findById(id)
                .map(article -> {
                    article.setTitle(updatedArticle.getTitle());
                    article.setDescription(updatedArticle.getDescription());
                    article.setSlug(updatedArticle.getSlug());
                    article.setContent(updatedArticle.getContent());
                    article.setAuthor(updatedArticle.getAuthor());
                    article.setTags(updatedArticle.getTags());
                    return articleRepository.save(article);
                })
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена"));
    }

    @Override
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    @Override
    public List<Article> getArticlesByAuthor(UUID authorId) {
        return List.of();
    }


    @Override
    public List<Article> getArticlesByTag(Long tagId) {
        return articleRepository.findByTags_Id(tagId);
    }

    @Override
    public List<Article> getArticlesByAuthor(Long authorId) {
        return articleRepository.findByAuthorId(authorId);
    }
}
