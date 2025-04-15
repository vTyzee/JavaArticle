package com.example.articles.controllers;

import com.example.articles.entities.Article;
import com.example.articles.entities.ArticleComment;
import com.example.articles.entities.Roles;
import com.example.articles.entities.User;
import com.example.articles.entities.Tag;
import com.example.articles.repositories.ArticleCommentRepository;
import com.example.articles.repositories.UserRepository;
import com.example.articles.service.ArticleService;
import com.example.articles.service.TagService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final TagService tagService;
    private final UserRepository userRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public ArticleController(ArticleService articleService,
                             TagService tagService,
                             UserRepository userRepository,
                             ArticleCommentRepository articleCommentRepository) {
        this.articleService = articleService;
        this.tagService = tagService;
        this.userRepository = userRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @GetMapping
    public String listArticles(Model model) {
        List<Article> articles = articleService.getAllArticles();
        model.addAttribute("articles", articles);
        model.addAttribute("currentUser", getCurrentUser());
        return "articles/list";
    }

    @GetMapping("/{id}")
    public String articleDetails(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));
        model.addAttribute("article", article);
        model.addAttribute("currentUser", getCurrentUser());
        return "articles/details";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("currentUser", getCurrentUser());
        model.addAttribute("tags", tagService.getAllTags());
        return "articles/add";
    }

    @PostMapping
    public String createArticle(@ModelAttribute("article") Article article,
                                @RequestParam("tagIds") List<Long> tagIds) {
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        article.setOwner(currentUser);
        Set<Tag> tags = new HashSet<>();
        tagIds.forEach(tagId -> tags.add(tagService.getTagById(tagId)));
        article.setTags(tags);
        articleService.createArticle(article);
        return "redirect:/articles";
    }

    @GetMapping("/by-month")
    public String getArticlesByMonth(@RequestParam(value = "month", required = false) String monthParam,
                                     Model model) {
        List<Article> articles;
        if (monthParam != null && !monthParam.isEmpty()) {
            try {
                YearMonth ym = YearMonth.parse(monthParam);
                articles = articleService.getArticlesByMonth(ym.getYear(), ym.getMonthValue());
            } catch (DateTimeParseException e) {
                articles = List.of();
            }
        } else {
            articles = articleService.getAllArticles();
        }
        model.addAttribute("articles", articles);
        model.addAttribute("currentUser", getCurrentUser());
        return "articles/list";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!currentUser.getRole().equals(Roles.ADMIN_ROLE) &&
                (article.getOwner() == null || !article.getOwner().getId().equals(currentUser.getId()))) {
            return "redirect:/access-denied";
        }
        model.addAttribute("article", article);
        model.addAttribute("tags", tagService.getAllTags());
        return "articles/edit";
    }

    @PostMapping("/update/{id}")
    public String updateArticle(@PathVariable Long id,
                                @ModelAttribute("article") Article updatedArticle,
                                @RequestParam("tagIds") List<Long> tagIds) {
        Article existingArticle = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!currentUser.getRole().equals(Roles.ADMIN_ROLE) &&
                (existingArticle.getOwner() == null || !existingArticle.getOwner().getId().equals(currentUser.getId()))) {
            return "redirect:/access-denied";
        }
        Set<Tag> tags = new HashSet<>();
        tagIds.forEach(tagId -> tags.add(tagService.getTagById(tagId)));
        updatedArticle.setTags(tags);
        updatedArticle.setOwner(existingArticle.getOwner());
        articleService.updateArticle(id, updatedArticle);
        return "redirect:/articles";
    }

    @GetMapping("/delete/{id}")
    public String deleteArticle(@PathVariable Long id) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        if (!currentUser.getRole().equals(Roles.ADMIN_ROLE) &&
                (article.getOwner() == null || !article.getOwner().getId().equals(currentUser.getId()))) {
            return "redirect:/access-denied";
        }
        articleService.deleteArticle(id);
        return "redirect:/articles";
    }

    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id, @RequestParam("body") String body) {
        Article article = articleService.getArticleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found with id " + id));
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
                !SecurityContextHolder.getContext().getAuthentication().isAuthenticated() ||
                "anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())) {
            return "redirect:/login";
        }
        User currentUser = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
        ArticleComment comment = new ArticleComment();
        comment.setBody(body);
        comment.setArticle(article);
        comment.setUser(currentUser);
        comment.setCreatedAt(LocalDateTime.now().withSecond(0).withNano(0));
        comment.setUpdatedAt(LocalDateTime.now().withSecond(0).withNano(0));
        articleCommentRepository.save(comment);
        return "redirect:/articles/" + id;
    }

    @GetMapping("/search")
    public String searchArticles(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "month", required = false) String monthParam,
            @RequestParam(value = "date", required = false) String dateParam,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "desc") String sortOrder,
            Model model) {

        List<Article> articles;

        // Если задан параметр date (в формате yyyy-MM-dd), то ищем по дате
        if (dateParam != null && !dateParam.isEmpty()) {
            try {
                // Парсим дату
                java.time.LocalDate date = java.time.LocalDate.parse(dateParam);
                // Формируем начало и конец дня
                LocalDateTime startOfDay = date.atStartOfDay();
                LocalDateTime endOfDay = date.atTime(23, 59, 59);
                articles = articleService.getArticlesByDay(startOfDay, endOfDay);
            } catch (Exception e) {
                articles = List.of();
            }
        } else if (monthParam != null && !monthParam.isEmpty()) {
            try {
                YearMonth ym = YearMonth.parse(monthParam);
                articles = articleService.getArticlesByMonth(ym.getYear(), ym.getMonthValue());
            } catch (Exception e) {
                articles = List.of();
            }
        } else if (query != null && !query.isEmpty()){
            articles = articleService.searchArticles(query);
        } else {
            articles = articleService.getAllArticles();
        }

        // Выполняем сортировку, если она требуется
        if ("asc".equalsIgnoreCase(sortOrder)) {
            articles.sort((a, b) -> a.getCreatedAt().compareTo(b.getCreatedAt()));
        } else {
            articles.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }

        model.addAttribute("currentUser", getCurrentUser());
        model.addAttribute("articles", articles);
        model.addAttribute("searchQuery", query);
        model.addAttribute("sortOrder", sortOrder);
        return "articles/list";
    }

    @GetMapping("/by-owner/{ownerId}")
    public String getArticlesByOwner(@PathVariable Long ownerId, Model model) {
        model.addAttribute("articles", articleService.getArticlesByOwner(ownerId));
        model.addAttribute("currentUser", getCurrentUser());
        return "articles/list";
    }

    @GetMapping("/by-tag/{tagId}")
    public String getArticlesByTag(@PathVariable Long tagId, Model model) {
        model.addAttribute("articles", articleService.getArticlesByTag(tagId));
        model.addAttribute("currentUser", getCurrentUser());
        return "articles/list";
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return userRepository.findByUsername(auth.getName());
    }
}
