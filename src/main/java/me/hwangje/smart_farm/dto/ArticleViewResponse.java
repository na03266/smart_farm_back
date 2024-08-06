package me.hwangje.smart_farm.dto;

import me.hwangje.smart_farm.domain.Article;

import java.time.LocalDateTime;

public class ArticleViewResponse {
    private String author;
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createAt;

    public ArticleViewResponse(Article article) {
        this.author = article.getAuthor();
        this.title = article.getTitle();
        this.id = article.getId();
        this.content = article.getContent();
        this.createAt = article.getCreatedAt();
    }
}
