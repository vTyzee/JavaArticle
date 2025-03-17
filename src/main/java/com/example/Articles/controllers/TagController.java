package com.example.Articles.controllers;

import com.example.Articles.entity.Tag;
import com.example.Articles.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // 📌 Получить все теги
    @GetMapping
    public String getAllTags(Model model) {
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);
        return "tags/list"; // Убедись, что у тебя есть файл templates/tags/list.html
    }

    // 📌 Получить тег по ID
    @GetMapping("/{id}")
    public String getTagById(@PathVariable Long id, Model model) {
        Tag tag = tagService.getTagById(id);
        model.addAttribute("tag", tag);
        return "tags/details"; // Убедись, что у тебя есть файл templates/tags/details.html
    }

    // 📌 Форма для добавления нового тега
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("tag", new Tag());
        return "tags/add"; // Убедись, что у тебя есть файл templates/tags/add.html
    }

}
