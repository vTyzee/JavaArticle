package com.example.articles.controllers;

import com.example.articles.entities.Article;
import com.example.articles.entities.Author;
import com.example.articles.entities.Tag;
import com.example.articles.entities.User;
import com.example.articles.service.ArticleService;
import com.example.articles.service.AuthorService;
import com.example.articles.service.TagService;
import com.example.articles.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final AuthorService authorService;
    private final TagService tagService;
    private final UserRepository userRepository;

    @Autowired
    public ArticleController(ArticleService articleService,
                             AuthorService authorService,
                             TagService tagService,
                             UserRepository userRepository) {
        this.articleService = articleService;
        this.authorService = authorService;
        this.tagService = tagService;
        this.userRepository = userRepository;
    }

    // GET /articles – показать список всех статей (с сортировкой по updatedAt DESC)
    @GetMapping
    public String listArticles(Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
        // Передаем текущего пользователя (если авторизован)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            User currentUser = userRepository.findByUsername(auth.getName());
            model.addAttribute("currentUser", currentUser);
        }
        return "articles/list";
    }

    // GET /articles/{id} – показать статью по ID
    @GetMapping("/{id}")
    public String articleDetails(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));
        model.addAttribute("article", article);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            User currentUser = userRepository.findByUsername(auth.getName());
            model.addAttribute("currentUser", currentUser);
        }
        return "articles/details";
    }

    // GET /articles/new – форма добавления статьи
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("tags", tagService.getAllTags());
        return "articles/add";
    }

    // POST /articles – добавить новую статью
    @PostMapping
    public String createArticle(@ModelAttribute("article") Article article,
                                @RequestParam("authorId") Long authorId,
                                @RequestParam("tagIds") List<Long> tagIds) {
        Author author = authorService.getAuthorById(authorId);
        article.setAuthor(author);
        Set<Tag> tags = new HashSet<>();
        for (Long tagId : tagIds) {
            Tag tag = tagService.getTagById(tagId);
            tags.add(tag);
        }
        article.setTags(tags);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        article.setOwner(currentUser);
        articleService.createArticle(article);
        return "redirect:/articles";
    }

    // GET /articles/edit/{id} – форма редактирования статьи
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        // Проверяем права (либо администратор, либо владелец)
        if (!currentUser.getRole().equals(User.Roles.ADMIN_ROLE) &&
                (article.getOwner() == null || !article.getOwner().getId().equals(currentUser.getId()))) {
            return "redirect:/access-denied";
        }
        model.addAttribute("article", article);
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("tags", tagService.getAllTags());
        return "articles/edit";
    }

    // POST /articles/update/{id} – обновить статью
    @PostMapping("/update/{id}")
    public String updateArticle(@PathVariable Long id,
                                @ModelAttribute("article") Article updatedArticle,
                                @RequestParam("authorId") Long authorId,
                                @RequestParam("tagIds") List<Long> tagIds) {
        Article existingArticle = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        if (!currentUser.getRole().equals(User.Roles.ADMIN_ROLE) &&
                (existingArticle.getOwner() == null || !existingArticle.getOwner().getId().equals(currentUser.getId()))) {
            return "redirect:/access-denied";
        }
        Author author = authorService.getAuthorById(authorId);
        updatedArticle.setAuthor(author);
        Set<Tag> tags = new HashSet<>();
        for (Long tagId : tagIds) {
            Tag tag = tagService.getTagById(tagId);
            tags.add(tag);
        }
        updatedArticle.setTags(tags);
        // Сохраняем владельца из существующей статьи
        updatedArticle.setOwner(existingArticle.getOwner());
        articleService.updateArticle(id, updatedArticle);
        return "redirect:/articles";
    }

    // GET /articles/delete/{id} – удалить статью
    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username);
        if (!currentUser.getRole().equals(User.Roles.ADMIN_ROLE) &&
                (article.getOwner() == null || !article.getOwner().getId().equals(currentUser.getId()))) {
            return "redirect:/access-denied";
        }
        articleService.deleteArticle(id);
        return "redirect:/articles";
    }

    // GET /articles/by-month?month=... – поиск статей по месяцу публикации
    @GetMapping("/by-month")
    public String getArticlesByMonth(@RequestParam("month") int month, Model model) {
        List<Article> articles = articleService.getArticlesByCreatedMonth(month);
        model.addAttribute("articles", articles);
        // Можно добавить текущего пользователя, если нужно
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            User currentUser = userRepository.findByUsername(auth.getName());
            model.addAttribute("currentUser", currentUser);
        }
        return "articles/list";
    }

    // GET /articles/by-author/{authorId} – показать статьи по автору
    @GetMapping("/by-author/{authorId}")
    public String getArticlesByAuthor(@PathVariable Long authorId, Model model) {
        List<Article> articles = articleService.getArticlesByAuthor(authorId);
        model.addAttribute("articles", articles);
        return "articles/list";
    }

    // GET /articles/by-tag/{tagId} – показать статьи по тегу
    @GetMapping("/by-tag/{tagId}")
    public String getArticlesByTag(@PathVariable Long tagId, Model model) {
        List<Article> articles = articleService.getArticlesByTag(tagId);
        model.addAttribute("articles", articles);
        return "articles/list";
    }

    // GET /articles/search?q=... – поиск статей по заголовку или содержимому
    @GetMapping("/search")
    public String searchArticles(@RequestParam("q") String query, Model model) {
        List<Article> articles = articleService.searchArticles(query);
        model.addAttribute("articles", articles);
        return "articles/list";
    }
}
