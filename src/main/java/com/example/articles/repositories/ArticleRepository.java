package com.example.articles.repositories;

import com.example.articles.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByOwnerId(Long ownerId);
    List<Article> findByTags_Id(Long tagId);
    void deleteByOwnerId(Long ownerId);
    List<Article> findByTitleContainingIgnoreCaseOrBodyContainingIgnoreCase(String title, String body);

    @Query("SELECT a FROM Article a " +
            "WHERE FUNCTION('YEAR', a.createdAt) = :year " +
            "  AND FUNCTION('MONTH', a.createdAt) = :month " +
            "ORDER BY a.createdAt ASC")
    List<Article> findByYearAndMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT a FROM Article a WHERE a.createdAt BETWEEN :start AND :end ORDER BY a.createdAt ASC")
    List<Article> findByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
