package com.owofurry.furry.img.service;

import com.owofurry.furry.img.vo.R;

/**
 * 管理文件记录服务，root 用户专用
 *
 * @author 果冻
 * @date 2024/04/26
 */
public interface AdminFileRecordService {
    R listNotCheck(int page, int size);

    void deleteRecord(Long... fileId);

    int checked(Long fileId);
}
