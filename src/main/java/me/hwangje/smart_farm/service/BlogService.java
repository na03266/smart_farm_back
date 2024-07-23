package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Article;
import me.hwangje.smart_farm.dto.AddArticleRequest;
import me.hwangje.smart_farm.repository.BlogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll(){
        return blogRepository.findAll();
    }
}
