package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
