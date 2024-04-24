package com.owofurry.furry.img.service;

import com.owofurry.furry.img.vo.R;

import java.util.List;

public interface KeywordService {
    void add(List<String> keyword);

    R list(int page, int size);

    R search(String keyword, int page, int size);
}
