package com.example.Articles.repository;

import com.example.Articles.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content);
    List<Article> findByAuthorId(Long authorId);
    List<Article> findByTags_Id(Long tagId);
}
