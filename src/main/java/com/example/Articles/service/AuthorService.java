package com.example.Articles.service;

import com.example.Articles.entity.Author;
import java.util.List;
import java.util.UUID;

public interface AuthorService {
    List<Author> getAllAuthors();
    Author getAuthorById(UUID id);
}
