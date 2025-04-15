package com.example.articles.service;

import com.example.articles.entities.Tag;
import com.example.articles.repositories.ArticleRepository;
import com.example.articles.repositories.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final ArticleRepository articleRepository;

    public TagServiceImpl(TagRepository tagRepository, ArticleRepository articleRepository) {
        this.tagRepository = tagRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public Tag getTagById(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Тег не найден"));
    }

    @Override
    public Tag createTag(Tag tag) {
        tag.setCreatedAt(LocalDateTime.now());
        return tagRepository.save(tag);
    }

    @Override
    public Tag updateTag(Long id, Tag tag) {
        return tagRepository.findById(id)
                .map(existingTag -> {
                    existingTag.setName(tag.getName());
                    return tagRepository.save(existingTag);
                })
                .orElseThrow(() -> new IllegalArgumentException("Тег не найден"));
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }
}
