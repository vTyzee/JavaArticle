package com.example.articles.repositories;

import com.example.articles.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // Найти статьи по ID автора
    @Query("SELECT a FROM Article a WHERE a.author.id = :authorId")
    List<Article> findByAuthorId(@Param("authorId") Long authorId);

    // Найти статьи, содержащие указанный тег (по ID тега)
    @Query("SELECT a FROM Article a JOIN a.tags t WHERE t.id = :tagId")
    List<Article> findByTagId(@Param("tagId") Long tagId);

    // Поиск статей по части заголовка или содержимого (без учета регистра)
    @Query("SELECT a FROM Article a WHERE lower(a.title) LIKE lower(concat('%', :q, '%')) OR lower(a.body) LIKE lower(concat('%', :q, '%'))")
    List<Article> searchArticles(@Param("q") String query);

    // Найти статьи, опубликованные в определенном месяце (например, 1 — январь)
    @Query("SELECT a FROM Article a WHERE MONTH(a.createdAt) = :month")
    List<Article> findByCreatedMonth(@Param("month") int month);

}
