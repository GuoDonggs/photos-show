package com.owofurry.furry.img.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.owofurry.furry.img.entity.FileRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileRecordMapper extends BaseMapper<FileRecord> {
    FileRecord selectForUpdate(Long fileId);


    int incrLoverNum(Long fileId);

    int decrLoverNum(Long fileId);
}
