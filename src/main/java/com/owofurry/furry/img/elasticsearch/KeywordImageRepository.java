package com.owofurry.furry.img.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface KeywordImageRepository extends ElasticsearchRepository<KeywordImageDoc, Long> {
    List<KeywordImageDoc> findKeywordImageByAllStrLikeIgnoreCase(String keyword, Pageable pageable);
}
