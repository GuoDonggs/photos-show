package com.owofurry.furry.img.service;

/**
 * 任务缓冲区服务
 *
 * @author 果冻
 * @date 2024/04/20
 */
public interface TasksBufferService {
    void addTask(Runnable task);
}
