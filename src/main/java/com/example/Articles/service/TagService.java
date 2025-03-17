package com.example.Articles.service;

import com.example.Articles.entity.Tag;
import java.util.List;

public interface TagService {
    List<Tag> getAllTags();
    Tag getTagById(Long id);
}
