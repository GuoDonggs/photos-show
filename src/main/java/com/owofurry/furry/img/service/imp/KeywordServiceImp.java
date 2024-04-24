package com.owofurry.furry.img.service.imp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.owofurry.furry.img.elasticsearch.KeywordDoc;
import com.owofurry.furry.img.elasticsearch.KeywordRepository;
import com.owofurry.furry.img.entity.Keyword;
import com.owofurry.furry.img.mapper.KeywordMapper;
import com.owofurry.furry.img.service.KeywordService;
import com.owofurry.furry.img.utils.RUtil;
import com.owofurry.furry.img.vo.R;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class KeywordServiceImp implements KeywordService {

    KeywordMapper keywordMapper;

    KeywordRepository keywordRepository;

    public KeywordServiceImp(KeywordMapper keywordMapper, KeywordRepository keywordRepository) {
        this.keywordMapper = keywordMapper;
        this.keywordRepository = keywordRepository;
    }

    @PostConstruct
    public void init() {
        int pageSize = 100;
        int page = 1;
        log.info("开始初始化关键字");
        Page<Keyword> iPage = new Page<>(page, pageSize);
        List<Keyword> records = keywordMapper.selectPage(iPage, null).getRecords();
        List<KeywordDoc> docs;
        keywordRepository.deleteAll();
        while (!records.isEmpty()) {
            docs = records.stream().map(KeywordDoc::new).toList();
            keywordRepository.saveAll(docs);
            page++;
            iPage = new Page<>(page, pageSize);
            records = keywordMapper.selectPage(iPage, null).getRecords();
        }
        log.info("关键字初始化完成, 总数: {}", keywordRepository.count());
    }

    @Override
    @CacheEvict(cacheNames = "keyword", allEntries = true)
    public void add(List<String> keywords) {
        int c = keywordMapper.insert(keywords);
        if (c > 0) {
            keywordRepository.saveAll(keywords.stream().map(KeywordDoc::new).toList());
        }
    }

    @Override
    @Cacheable(cacheNames = "keyword:list")
    public R list(int page, int size) {
        Page<Keyword> iPage = new Page<>(page, size);
        return RUtil.ok(keywordMapper.selectPage(iPage, null).getRecords());
    }

    @Override
    @Cacheable(cacheNames = "keyword:search", key = "#keyword + ':' + #page + ':' + #size")
    public R search(String keyword, int page, int size) {
        if (page - 1 >= 0) {
            page--;
        }
        return RUtil.ok(keywordRepository.findKeywordDocByKeywordLike(keyword, PageRequest.of(page, size)));
    }
}
