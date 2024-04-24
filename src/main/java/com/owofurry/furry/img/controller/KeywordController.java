package com.owofurry.furry.img.controller;

import com.owofurry.furry.img.exception.UserOperationException;
import com.owofurry.furry.img.service.KeywordService;
import com.owofurry.furry.img.vo.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/keyword")
public class KeywordController {
    KeywordService keywordService;

    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @GetMapping("/search/{keyword}")
    public R search(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                    @RequestParam(value = "size", required = false, defaultValue = "0") int size,
                    @PathVariable("keyword") String keyword) {
        if (page < 0 || size < 0 || size > 30) {
            throw new UserOperationException("参数错误");
        }
        return keywordService.search(keyword, size, 10);
    }
}
