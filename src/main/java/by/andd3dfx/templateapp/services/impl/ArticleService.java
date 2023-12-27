package by.andd3dfx.templateapp.services.impl;

import by.andd3dfx.templateapp.dto.ArticleDto;
import by.andd3dfx.templateapp.dto.ArticleUpdateDto;
import by.andd3dfx.templateapp.exceptions.ArticleNotFoundException;
import by.andd3dfx.templateapp.mappers.ArticleMapper;
import by.andd3dfx.templateapp.persistence.dao.ArticleRepository;
import by.andd3dfx.templateapp.persistence.entities.Article;
import by.andd3dfx.templateapp.services.IArticleService;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleService implements IArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    @Transactional
    @Override
    public ArticleDto create(ArticleDto articleDto) {
        LocalDateTime now = LocalDateTime.now();
        articleDto.setDateCreated(now);
        articleDto.setDateUpdated(now);

        Article entity = articleMapper.toArticle(articleDto);
        Article savedEntity = articleRepository.save(entity);
        return articleMapper.toArticleDto(savedEntity);
    }

    @Transactional(readOnly = true)
    @Override
    public ArticleDto get(String id) {
        return articleRepository.findById(id)
            .map(articleMapper::toArticleDto)
            .orElseThrow(() -> new ArticleNotFoundException(id));
    }

    @Transactional
    @Override
    public void update(String id, ArticleUpdateDto articleUpdateDto) {
        articleRepository.findById(id)
            .map(article -> {
                articleMapper.toArticle(articleUpdateDto, article);
                article.setDateUpdated(LocalDateTime.now());
                Article savedArticle = articleRepository.save(article);
                return articleMapper.toArticleDto(savedArticle);
            }).orElseThrow(() -> new ArticleNotFoundException(id));
    }

    @Transactional
    @Override
    public void delete(String id) {
        try {
            articleRepository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ArticleNotFoundException(id);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Slice<ArticleDto> getAll(Pageable pageable) {
        Slice<Article> pagedResult = articleRepository.findAll(pageable);
        return pagedResult.map(articleMapper::toArticleDto);
    }
}
