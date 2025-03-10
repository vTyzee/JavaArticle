package com.example.Articles.controllers;

import com.example.Articles.entity.Article;
import com.example.Articles.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/articles")
public class ArticleController {
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public String getAllArticles(Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
        return "articles/list";
    }

    @GetMapping("/{id}")
    public String getArticleById(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена"));
        model.addAttribute("article", article);
        return "articles/details";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        return "articles/form";
    }

    @PostMapping
    public String createArticle(@ModelAttribute Article article) {
        articleService.createArticle(article);
        return "redirect:/articles";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Статья не найдена"));
        model.addAttribute("article", article);
        return "articles/form";
    }

    @PostMapping("/update/{id}")
    public String updateArticle(@PathVariable Long id, @ModelAttribute Article article) {
        articleService.updateArticle(id, article);
        return "redirect:/articles";
    }

    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return "redirect:/articles";
    }

    @GetMapping("/by-author/{authorId}")
    public String getArticlesByAuthor(@PathVariable UUID authorId, Model model) { // ✅ Исправлено
        List<Article> articles = articleService.getArticlesByAuthor(authorId);
        model.addAttribute("articles", articles);
        return "articles/list";
    }

    @GetMapping("/by-tag/{tagId}")
    public String getArticlesByTag(@PathVariable Long tagId, Model model) {
        List<Article> articles = articleService.getArticlesByTag(tagId);
        model.addAttribute("articles", articles);
        return "articles/list";
    }

    @GetMapping("/search")
    public String searchArticles(@RequestParam String query, Model model) {
        List<Article> articles = articleService.searchArticles(query);
        model.addAttribute("articles", articles);
        return "articles/list";
    }
}
