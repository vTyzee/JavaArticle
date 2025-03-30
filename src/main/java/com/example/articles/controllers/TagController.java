package com.example.articles.controllers;

import com.example.articles.entities.Tag;
import com.example.articles.service.TagService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    // GET /tags — показать список тегов (HTML)
    @GetMapping
    public String listTags(Model model) {
        model.addAttribute("tags", tagService.getAllTags());
        return "tags/list"; // имя Thymeleaf-шаблона, например, src/main/resources/templates/tags/list.html
    }

    // GET /tags/new — форма добавления тега
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("tag", new Tag());
        return "tags/add"; // шаблон для формы добавления тега
    }

    // POST /tags — добавить тег
    @PostMapping
    public String createTag(@ModelAttribute Tag tag) {
        tag.setCreatedAt(LocalDateTime.now());
        tagService.createTag(tag);
        return "redirect:/tags";
    }

    // GET /tags/delete/{id} — удалить тег
    @GetMapping("/delete/{id}")
    public String deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return "redirect:/tags";
    }
}
