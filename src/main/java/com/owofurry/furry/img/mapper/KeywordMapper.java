package com.owofurry.furry.img.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.owofurry.furry.img.entity.Keyword;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author gs
 * @description 针对表【keyword】的数据库操作Mapper
 * @createDate 2024-04-20 09:53:20
 * @Entity com.owofurry.furry.img.entity.Keyword
 */
@Mapper
public interface KeywordMapper extends BaseMapper<Keyword> {
    int insert(List<String> keywords);
}




