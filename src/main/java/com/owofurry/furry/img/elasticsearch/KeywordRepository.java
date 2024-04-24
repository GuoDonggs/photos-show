package com.owofurry.furry.img.elasticsearch;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface KeywordRepository extends ElasticsearchRepository<KeywordDoc, Long> {
    List<KeywordDoc> findKeywordDocByKeywordLike(String keyword, Pageable pageable);
}
