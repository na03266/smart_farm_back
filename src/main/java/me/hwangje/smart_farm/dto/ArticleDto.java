package me.hwangje.smart_farm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hwangje.smart_farm.domain.Article;

public class ArticleDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class AddArticleRequest {
        private String title;
        private String content;

        public Article toEntity(String author) {
            return Article.builder()
                    .title(title)
                    .content(content)
                    .author(author)
                    .build();
        }
    }

    @Getter
    public static class ArticleResponse {
        private final String title;
        private final String content;

        public ArticleResponse(Article article) {
            this.title = article.getTitle();
            this.content = article.getContent();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UpdateArticleRequest {
        private String title;
        private String content;
    }

}