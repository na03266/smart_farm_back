package me.hwangje.smart_farm.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Article;
import me.hwangje.smart_farm.dto.AddArticleRequest;
import me.hwangje.smart_farm.dto.UpdateArticleRequest;
import me.hwangje.smart_farm.repository.BlogRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    public List<Article> findAll(){
        return blogRepository.findAll();
    }

    public Article findById(long id){
        return  blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found " + id));
    }

    public void delete(long id){
        Article article = blogRepository.findById(id)
                        .orElseThrow(()-> new IllegalArgumentException("not found" + id));
        authorizeArticleAuthor(article);
        blogRepository.deleteById(id);
    }

    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)){
            throw new IllegalArgumentException("not authorized");
        }
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request){
        Article article = blogRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found " + id));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }
}
