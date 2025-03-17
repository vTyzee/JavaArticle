package com.example.Articles.repository;

import com.example.Articles.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
}
