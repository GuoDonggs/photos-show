package com.owofurry.furry.img.service;


import com.owofurry.furry.img.entity.FileRecord;
import com.owofurry.furry.img.vo.R;

public interface FileRecordService {

    /**
     * 热门图片
     *
     * @param size 尺寸
     * @return 右
     */
    R hot(int size);

    R listFile(int page, int size);

    FileRecord findByFileId(Long fileId);

    R listByUser(int user_id, int page, int size);

    void update(FileRecord record);

    void addRecord(FileRecord record);

    void deleteRecord(Long... fileId);

    void deleteRecord(Integer userId, Long... fileId);

    R search(String keyword, int page, int size);

    void incrLoverNum(Long fileId);

    void decrLoverNum(Long fileId);

}
