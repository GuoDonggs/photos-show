package com.owofurry.furry.img.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.owofurry.furry.img.entity.FileLover;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author gs
 * @description 针对表【file_lover】的数据库操作Mapper
 * @createDate 2024-04-15 22:36:17
 * @Entity com.owofurry.furry.img.entity.FileLover
 */
@Mapper
public interface FileLoverMapper extends BaseMapper<FileLover> {
    @Override
    int insert(FileLover entity);
}




