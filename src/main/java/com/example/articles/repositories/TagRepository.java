package com.example.articles.repositories;

import com.example.articles.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Tag findByName(String name);

    List<Tag> findByNameContainingIgnoreCase(String namePart);
}
