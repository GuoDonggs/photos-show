package com.owofurry.furry.img.service;

import com.owofurry.furry.img.entity.FileLover;

import java.util.List;

public interface LoverService {
    List<FileLover> findLiverByUser(Integer userId);

    List<FileLover> findLoverByFile(Long fileId);

    boolean delete(Long fileId, Integer userId);

    boolean delete(Long fileId);

    boolean save(Long fileId, Integer userId);

    boolean exist(Long fileId, Integer userId);
}
